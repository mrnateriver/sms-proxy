package io.mrnateriver.smsproxy.shared

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.util.logging.Level
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class SmsProcessingConfig(
    val maxRetries: UShort,
    val timeout: Duration = 30.seconds,
)

class SmsProcessingService(
    private val repository: SmsRepository,
    private val relay: SmsRelayService,
    private val observability: ObservabilityService,
    private val config: SmsProcessingConfig = SmsProcessingConfig(3u),
    private val clock: Clock = Clock.System,
) {
    suspend fun process(sms: SmsData): SmsEntry {
        return observability.runSpan("SmsProcessingService.process") {
            val entry = repository.insert(sms)
            processEntry(entry)
        }
    }

    suspend fun handleUnprocessedMessages(): Iterable<SmsEntry> = coroutineScope {
        observability.runSpan("SmsProcessingService.handleUnprocessedMessages") {
            val entries = repository.getAll(
                SmsRelayStatus.ERROR,
                SmsRelayStatus.PENDING,
                SmsRelayStatus.IN_PROGRESS
            )
            observability.log(Level.INFO, "processing ${entries.size} entries")

            entries.map { async { processEntry(it) } }.awaitAll()
        }
    }

    private suspend fun processEntry(entry: SmsEntry): SmsEntry =
        withContext(Dispatchers.IO) {
            observability.runSpan("SmsProcessingService.processEntry") {
                try {
                    checkStatus(entry) { return@runSpan it }
                    checkTimeout(entry) { return@runSpan it }
                    checkRetries(entry) { return@runSpan it }

                    startProcessing(entry)

                    recordProcessingSuccess(entry)
                } catch (e: Exception) {
                    recordProcessingError(entry, e)
                }
            }
        }

    private suspend fun recordProcessingSuccess(entry: SmsEntry): SmsEntry {
        return repository.update(entry.copy(sendStatus = SmsRelayStatus.SUCCESS))
    }

    private suspend fun recordProcessingError(
        entry: SmsEntry,
        e: Exception,
    ): SmsEntry {
        observability.log(Level.WARNING, "failed to process entry ${entry.guid}: $e")
        return repository.update(
            entry.copy(sendStatus = SmsRelayStatus.ERROR, sendFailureReason = e.toString()),
        )
    }

    private suspend fun startProcessing(entry: SmsEntry) {
        observability.log(Level.INFO, "relaying entry ${entry.guid}")
        relay.relay(
            repository.update(
                entry.copy(
                    sendStatus = SmsRelayStatus.IN_PROGRESS,
                    sendRetries = entry.sendRetries.inc()
                ),
            ),
        )
    }

    private inline fun checkStatus(entry: SmsEntry, cont: (entry: SmsEntry) -> Unit) {
        if (entry.sendStatus == SmsRelayStatus.FAILED || entry.sendStatus == SmsRelayStatus.SUCCESS) {
            cont(entry)
        }
    }

    private inline fun checkTimeout(entry: SmsEntry, cont: (entry: SmsEntry) -> Unit) {
        if (entry.sendStatus == SmsRelayStatus.IN_PROGRESS) {
            if (entry.updatedAt != null && entry.updatedAt.plus(config.timeout) >= clock.now()) {
                observability.log(Level.WARNING, "entry ${entry.guid} is stuck in progress")
                throw IllegalStateException("entry is stuck in progress")
            } else {
                cont(entry)
            }
        }
    }

    private suspend inline fun checkRetries(entry: SmsEntry, cont: (entry: SmsEntry) -> Unit) {
        val retries = entry.sendRetries
        if (retries >= config.maxRetries) {
            observability.log(Level.WARNING, "entry ${entry.guid} reached max retries")
            cont(
                repository.update(
                    entry.copy(
                        sendStatus = SmsRelayStatus.FAILED,
                        sendFailureReason = "Reached max retries",
                    ),
                ),
            )
        }
    }
}