package io.mrnateriver.smsproxy.relay.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
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
import javax.inject.Singleton
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract

private const val METRICS_NAME_PROCESSING_FAILURES = "processing_failures"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "messages_stats")

private val KEY_PROCESSING_FAILURES = intPreferencesKey(METRICS_NAME_PROCESSING_FAILURES)
private val KEY_PROCESSING_FAILURE_TIMESTAMP = longPreferencesKey("processing_failure_timestamp")

@Singleton
class MessageStatsService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val observabilityService: ObservabilityService,
    private val messagesRepository: MessageRepositoryContract,
) {
    private val updateTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    val statsUpdates = updateTrigger.asSharedFlow()

    fun triggerUpdate() {
        updateTrigger.tryEmit(Unit)
    }

    suspend fun incrementProcessingFailures() = supervisorScope {
        // We don't want to fail storage operation if observability fails, hence the supervisorScope
        launch { observabilityService.incrementCounter(METRICS_NAME_PROCESSING_FAILURES) }

        context.dataStore.edit { preferences ->
            val currentFailures = preferences[KEY_PROCESSING_FAILURES] ?: 0
            val now = Clock.System.now()

            observabilityService.log(
                Level.FINEST,
                "Updating failure count: ${currentFailures + 1} at $now"
            )
            preferences[KEY_PROCESSING_FAILURES] = currentFailures + 1
            preferences[KEY_PROCESSING_FAILURE_TIMESTAMP] = now.toEpochMilliseconds()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getStats(): Flow<MessageStatsData> {
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

    fun getProcessingErrors(): Flow<StatsEntry> {
        return context.dataStore.data.map { preferences ->
            val value = preferences[KEY_PROCESSING_FAILURES] ?: 0
            val tsValue = preferences[KEY_PROCESSING_FAILURE_TIMESTAMP]
            val ts = if (tsValue != null) {
                Instant.fromEpochMilliseconds(tsValue)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
            } else {
                null
            }

            StatsEntry(value, ts)
        }
    }

    fun getProcessingFailures(): Flow<StatsEntry> {
        return updateTrigger.map { getMessageEntryCountByStatus(MessageRelayStatus.FAILED) }
    }

    fun getProcessedMessages(): Flow<StatsEntry> {
        return updateTrigger.map {
            coroutineScope {
                val (count, lastEntries) = listOf(
                    async { messagesRepository.getCount() },
                    async { messagesRepository.getLastEntries(1) },
                ).awaitAll()

                val lastEntry = (lastEntries as Iterable<*>).firstOrNull() as MessageEntry?
                StatsEntry(
                    count as Int,
                    lastEntry?.updatedAt?.toLocalDateTime(TimeZone.currentSystemDefault())
                )
            }
        }
    }

    fun getRelayedMessages(): Flow<StatsEntry> {
        return updateTrigger.map { getMessageEntryCountByStatus(MessageRelayStatus.SUCCESS) }
    }

    private suspend fun getMessageEntryCountByStatus(vararg status: MessageRelayStatus): StatsEntry =
        coroutineScope {
            val (count, entry) = listOf(
                async { messagesRepository.getCountByStatus(*status) },
                async { messagesRepository.getLastEntryByStatus(*status) },
            ).awaitAll()

            StatsEntry(
                count as Int,
                (entry as MessageEntry?)?.updatedAt?.toLocalDateTime(TimeZone.currentSystemDefault())
            )
        }

    data class StatsEntry(
        val value: Int,
        val lastEvent: LocalDateTime?,
    )
}

