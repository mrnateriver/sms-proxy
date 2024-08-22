package io.mrnateriver.smsproxy.shared.services

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
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService as MessageRelayServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

data class MessageProcessingConfig(
    val maxRetries: Int,
    val timeout: Duration = 30.seconds,
)

class MessageProcessingService(
    private val repository: MessageRepositoryContract,
    private val relay: MessageRelayServiceContract,
    private val observability: ObservabilityServiceContract,
    private val config: MessageProcessingConfig = MessageProcessingConfig(3),
    private val clock: Clock = Clock.System,
) : MessageProcessingServiceContract {
    override suspend fun process(msg: MessageData): MessageEntry {
        return observability.runSpan("MessageProcessingService.process") {
            val entry = repository.insert(msg)
            val (result, exception) = processEntry(entry)
            if (exception != null) {
                throw exception
            }
            result
        }
    }

    override suspend fun handleUnprocessedMessages(): Iterable<MessageEntry> = coroutineScope {
        observability.runSpan("MessageProcessingService.handleUnprocessedMessages") {
            val entries = repository.getAll(
                MessageRelayStatus.ERROR,
                MessageRelayStatus.PENDING,
                MessageRelayStatus.IN_PROGRESS
            )
            observability.log(Level.INFO, "Processing ${entries.size} entries")

            entries.map { async { processEntry(it).component1() } }.awaitAll()
        }
    }

    private suspend fun processEntry(entry: MessageEntry): Pair<MessageEntry, Exception?> =
        withContext(Dispatchers.IO) {
            observability.runSpan("MessageProcessingService.processEntry") {
                var updated = entry
                try {
                    checkStatus(updated) { return@runSpan it to null }
                    checkTimeout(updated) { return@runSpan it to null }

                    updated = startProcessing(updated)
                    relay.relay(updated)

                    recordProcessingSuccess(updated) to null
                } catch (e: Exception) {
                    recordProcessingError(updated, e) to e
                }
            }
        }

    private suspend fun recordProcessingSuccess(entry: MessageEntry): MessageEntry {
        return repository.update(entry.copy(sendStatus = MessageRelayStatus.SUCCESS))
    }

    private suspend fun startProcessing(entry: MessageEntry): MessageEntry {
        observability.log(Level.INFO, "Relaying entry ${entry.guid}")
        return repository.update(
            entry.copy(
                sendStatus = MessageRelayStatus.IN_PROGRESS,
                sendRetries = entry.sendRetries.inc()
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
            if (entry.updatedAt != null && entry.updatedAt.plus(config.timeout) < clock.now()) {
                observability.log(Level.WARNING, "Entry ${entry.guid} is stuck in progress")
                throw IllegalStateException("Entry is stuck in progress")
            } else {
                cont(entry)
            }
        }
    }

    private suspend fun recordProcessingError(
        entry: MessageEntry,
        exception: Exception,
    ): MessageEntry {
        observability.reportException(exception)

        observability.log(Level.WARNING, "Failed to process entry ${entry.guid}: $exception")

        val retries = entry.sendRetries
        if (retries >= config.maxRetries) {
            observability.log(Level.WARNING, "Entry ${entry.guid} reached max retries")
            return repository.update(
                entry.copy(
                    sendStatus = MessageRelayStatus.FAILED,
                    sendFailureReason = "Reached max retries",
                ),
            )
        } else {
            return repository.update(
                entry.copy(
                    sendStatus = MessageRelayStatus.ERROR,
                    sendFailureReason = exception.toString()
                ),
            )
        }
    }
}