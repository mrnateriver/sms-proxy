package io.mrnateriver.smsproxy.shared

import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.util.logging.Level
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class MessageProcessingConfig(
    val maxRetries: UShort,
    val timeout: Duration = 30.seconds,
)

class MessageProcessingService(
    private val repository: MessageRepository,
    private val relay: MessageRelayService,
    private val observability: ObservabilityService,
    private val config: MessageProcessingConfig = MessageProcessingConfig(3u),
    private val clock: Clock = Clock.System,
) {
    suspend fun process(msg: MessageData): MessageEntry {
        return observability.runSpan("MessageProcessingService.process") {
            val entry = repository.insert(msg)
            processEntry(entry)
        }
    }

    suspend fun handleUnprocessedMessages(): Iterable<MessageEntry> = coroutineScope {
        observability.runSpan("MessageProcessingService.handleUnprocessedMessages") {
            val entries = repository.getAll(
                MessageRelayStatus.ERROR,
                MessageRelayStatus.PENDING,
                MessageRelayStatus.IN_PROGRESS
            )
            observability.log(Level.INFO, "Processing ${entries.size} entries")

            entries.map { async { processEntry(it) } }.awaitAll()
        }
    }

    private suspend fun processEntry(entry: MessageEntry): MessageEntry =
        withContext(Dispatchers.IO) {
            observability.runSpan("MessageProcessingService.processEntry") {
                try {
                    checkStatus(entry) { return@runSpan it }
                    checkTimeout(entry) { return@runSpan it }
                    checkRetries(entry) { return@runSpan it }

                    startProcessing(entry)

                    recordProcessingSuccess(entry)
                } catch (e: Exception) {
                    recordProcessingError(entry, e)
                    throw e
                }
            }
        }

    private suspend fun recordProcessingSuccess(entry: MessageEntry): MessageEntry {
        return repository.update(entry.copy(sendStatus = MessageRelayStatus.SUCCESS))
    }

    private suspend fun recordProcessingError(
        entry: MessageEntry,
        e: Exception,
    ): MessageEntry {
        observability.log(Level.WARNING, "Failed to process entry ${entry.guid}: $e")
        return repository.update(
            entry.copy(sendStatus = MessageRelayStatus.ERROR, sendFailureReason = e.toString()),
        )
    }

    private suspend fun startProcessing(entry: MessageEntry) {
        observability.log(Level.INFO, "Relaying entry ${entry.guid}")
        relay.relay(
            repository.update(
                entry.copy(
                    sendStatus = MessageRelayStatus.IN_PROGRESS,
                    sendRetries = entry.sendRetries.inc()
                ),
            ),
        )
    }

    private inline fun checkStatus(entry: MessageEntry, cont: (entry: MessageEntry) -> Unit) {
        if (entry.sendStatus == MessageRelayStatus.FAILED || entry.sendStatus == MessageRelayStatus.SUCCESS) {
            cont(entry)
        }
    }

    private inline fun checkTimeout(entry: MessageEntry, cont: (entry: MessageEntry) -> Unit) {
        if (entry.sendStatus == MessageRelayStatus.IN_PROGRESS) {
            if (entry.updatedAt != null && entry.updatedAt.plus(config.timeout) >= clock.now()) {
                observability.log(Level.WARNING, "Entry ${entry.guid} is stuck in progress")
                throw IllegalStateException("Entry is stuck in progress")
            } else {
                cont(entry)
            }
        }
    }

    private suspend inline fun checkRetries(
        entry: MessageEntry,
        cont: (entry: MessageEntry) -> Unit,
    ) {
        val retries = entry.sendRetries
        if (retries >= config.maxRetries) {
            observability.log(Level.WARNING, "Entry ${entry.guid} reached max retries")
            cont(
                repository.update(
                    entry.copy(
                        sendStatus = MessageRelayStatus.FAILED,
                        sendFailureReason = "Reached max retries",
                    ),
                ),
            )
        }
    }
}