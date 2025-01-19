package io.mrnateriver.smsproxy.relay.services.usecases

import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsData
import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsEntry
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageStatsRepository as MessageStatsRepositoryContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageStatsService as MessageStatsServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

internal const val METRICS_NAME_PROCESSING_ERRORS = "processing_failures"

class MessageStatsService @Inject constructor(
    private val observabilityService: ObservabilityServiceContract,
    private val statsRepository: MessageStatsRepositoryContract,
    private val messagesRepository: MessageRepositoryContract,
) : MessageStatsServiceContract {
    private val updateTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    override fun triggerUpdate() {
        updateTrigger.tryEmit(Unit)
    }

    override suspend fun incrementProcessingErrors(): Unit = supervisorScope {
        // We don't want to fail storage operation if observability fails, hence the supervisorScope
        launch { observabilityService.incrementCounter(METRICS_NAME_PROCESSING_ERRORS) }
        statsRepository.incrementProcessingErrors()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getStats(): Flow<MessageStatsData> {
                getRelayedMessages(),
        return merge(messagesWatchService.watchLastEntries(1), updateTrigger)
            .flatMapMerge {
                combine(
                    statsRepository.getProcessingErrors(),
                    getProcessingFailures(),
                    getProcessedMessages(),
                ) { errors, failures, processed, relayed ->
                    MessageStatsData(
                        processed = processed,
                        relayed = relayed,
                        errors = errors,
                        failures = failures,
                    )
                }
            }
    }

    internal fun getProcessingFailures(): Flow<MessageStatsEntry> {
        return updateTrigger.map { getMessageEntryCountByStatus(MessageRelayStatus.FAILED) }
    }

    internal fun getRelayedMessages(): Flow<MessageStatsEntry> {
        return updateTrigger.map { getMessageEntryCountByStatus(MessageRelayStatus.SUCCESS) }
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
                    lastEntry?.updatedAt?.toLocalDateTime(TimeZone.currentSystemDefault()),
                )
            }
        }
    }

    private suspend fun getMessageEntryCountByStatus(vararg status: MessageRelayStatus): MessageStatsEntry =
        coroutineScope {
            val (count, entry) = listOf(
                async { messagesRepository.getCountByStatus(*status) },
                async { messagesRepository.getLastEntryByStatus(*status) },
            ).awaitAll()

            MessageStatsEntry(
                count as Int,
                (entry as MessageEntry?)?.updatedAt?.toLocalDateTime(TimeZone.currentSystemDefault()),
            )
        }
}
