package io.mrnateriver.smsproxy.relay.services

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.logging.Level
import javax.inject.Inject
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract

private const val METRICS_NAME_PROCESSING_ERRORS = "processing_failures"

private val KEY_PROCESSING_ERRORS = intPreferencesKey(METRICS_NAME_PROCESSING_ERRORS)
private val KEY_PROCESSING_ERROR_TIMESTAMP = longPreferencesKey("processing_failure_timestamp")

interface MessageStatsServiceContract {
    val statsUpdates: Flow<Unit>

    fun triggerUpdate()
    suspend fun incrementProcessingErrors()
    fun getStats(): Flow<MessageStatsData>
}

data class MessageStatsEntry(
    val value: Int,
    val lastEvent: LocalDateTime?,
)

class MessageStatsService @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val observabilityService: ObservabilityService,
    private val messagesRepository: MessageRepositoryContract,
    private val clock: Clock = Clock.System,
) : MessageStatsServiceContract {
    private val updateTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    override val statsUpdates: Flow<Unit> = updateTrigger.asSharedFlow()

    override fun triggerUpdate() {
        updateTrigger.tryEmit(Unit)
    }

    override suspend fun incrementProcessingErrors(): Unit = supervisorScope {
        // We don't want to fail storage operation if observability fails, hence the supervisorScope
        launch { observabilityService.incrementCounter(METRICS_NAME_PROCESSING_ERRORS) }

        dataStore.edit { preferences ->
            val currentFailures = preferences[KEY_PROCESSING_ERRORS] ?: 0
            val now = clock.now()

            observabilityService.log(
                Level.FINEST,
                "Updating failure count: ${currentFailures + 1} at $now"
            )
            preferences[KEY_PROCESSING_ERRORS] = currentFailures + 1
            preferences[KEY_PROCESSING_ERROR_TIMESTAMP] = now.toEpochMilliseconds()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getStats(): Flow<MessageStatsData> {
        return updateTrigger.flatMapLatest {
            combine(
                getProcessingErrors(),
                getProcessingFailures(),
                getProcessedMessages(),
                getRelayedMessages(),
            ) { errors, failures, processed, relayed ->
                MessageStatsData(
                    processed = processed.value,
                    relayed = relayed.value,
                    errors = errors.value,
                    failures = failures.value,
                    lastProcessedAt = processed.lastEvent,
                    lastRelayedAt = relayed.lastEvent,
                    lastErrorAt = errors.lastEvent,
                    lastFailureAt = failures.lastEvent,
                )
            }
        }
    }

    internal fun getProcessingErrors(): Flow<MessageStatsEntry> {
        return dataStore.data.map { preferences ->
            val value = preferences[KEY_PROCESSING_ERRORS] ?: 0
            val tsValue = preferences[KEY_PROCESSING_ERROR_TIMESTAMP]
            val ts = if (tsValue != null) {
                Instant.fromEpochMilliseconds(tsValue)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
            } else {
                null
            }

            MessageStatsEntry(value, ts)
        }
    }

    internal fun getProcessingFailures(): Flow<MessageStatsEntry> {
        return updateTrigger.map { getMessageEntryCountByStatus(MessageRelayStatus.FAILED) }
    }

    internal fun getProcessedMessages(): Flow<MessageStatsEntry> {
        return updateTrigger.map {
            coroutineScope {
                val (count, lastEntries) = listOf(
                    async { messagesRepository.getCount() },
                    async { messagesRepository.getLastEntries(1) },
                ).awaitAll()

                val lastEntry = (lastEntries as Iterable<*>).firstOrNull() as MessageEntry?
                MessageStatsEntry(
                    count as Int,
                    lastEntry?.updatedAt?.toLocalDateTime(TimeZone.currentSystemDefault())
                )
            }
        }
    }

    internal fun getRelayedMessages(): Flow<MessageStatsEntry> {
        return updateTrigger.map { getMessageEntryCountByStatus(MessageRelayStatus.SUCCESS) }
    }

    private suspend fun getMessageEntryCountByStatus(vararg status: MessageRelayStatus): MessageStatsEntry =
        coroutineScope {
            val (count, entry) = listOf(
                async { messagesRepository.getCountByStatus(*status) },
                async { messagesRepository.getLastEntryByStatus(*status) },
            ).awaitAll()

            MessageStatsEntry(
                count as Int,
                (entry as MessageEntry?)?.updatedAt?.toLocalDateTime(TimeZone.currentSystemDefault())
            )
        }
}

