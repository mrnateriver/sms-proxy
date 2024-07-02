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

// TODO: tests

data class SmsProcessingConfig(
    val maxRetries: UShort,
    val timeout: Duration = 30.seconds,
)

data class SmsProcessingStats(
    val success: Int,
    val errors: Int,
    val failures: Int,
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

            entry
        }
    }

    suspend fun handleUnprocessedMessages(): SmsProcessingStats = coroutineScope {
        observability.runSpan("SmsProcessingService.handleUnprocessedMessages") {
            val entries = repository.getAll(
                SmsRelayStatus.ERROR,
                SmsRelayStatus.PENDING,
                SmsRelayStatus.IN_PROGRESS
            )
            observability.log(Level.INFO, "processing ${entries.size} entries")

            var success = 0
            var errors = 0
            var failures = 0
            for (it in entries.map { async { processEntry(it) } }.awaitAll()) {
                when (it) {
                    SmsRelayStatus.SUCCESS -> success++
                    SmsRelayStatus.ERROR -> errors++
                    SmsRelayStatus.FAILED -> failures++
                    else -> Unit
                }
            }

            SmsProcessingStats(success, errors, failures)
        }
    }

    private suspend fun processEntry(entry: SmsEntry): SmsRelayStatus =
        withContext(Dispatchers.IO) {
            observability.runSpan("SmsProcessingService.processEntry") {
                try {
                    checkTimeout(entry) {
                        return@runSpan SmsRelayStatus.IN_PROGRESS
                    }

                    checkRetries(entry) {
                        return@runSpan SmsRelayStatus.FAILED
                    }

                    startProcessing(entry)

                    recordProcessingSuccess(entry)
                } catch (e: Exception) {
                    recordProcessingError(entry, e)
                }
            }
        }

    private suspend fun recordProcessingSuccess(entry: SmsEntry): SmsRelayStatus {
        repository.updateStatus(entry.guid, SmsRelayStatus.SUCCESS)
        return SmsRelayStatus.SUCCESS
    }

    private suspend fun recordProcessingError(
        entry: SmsEntry,
        e: Exception,
    ): SmsRelayStatus {
        observability.log(Level.WARNING, "failed to process entry ${entry.guid}: $e")
        repository.updateStatus(entry.guid, SmsRelayStatus.ERROR, e.toString())
        return SmsRelayStatus.ERROR
    }

    private suspend fun startProcessing(entry: SmsEntry) {
        observability.log(Level.INFO, "relaying entry ${entry.guid}")
        repository.startProgress(entry.guid)
        relay.relay(entry)
    }

    private inline fun checkTimeout(entry: SmsEntry, cont: () -> Unit) {
        if (entry.sendStatus == SmsRelayStatus.IN_PROGRESS) {
            if (entry.updatedAt != null && entry.updatedAt.plus(config.timeout) > clock.now()) {
                observability.log(Level.WARNING, "entry ${entry.guid} is stuck in progress")
                throw IllegalStateException("entry is stuck in progress")
            } else {
                cont()
            }
        }
    }

    private suspend inline fun checkRetries(entry: SmsEntry, cont: () -> Unit) {
        val retries = entry.sendRetries
        if (retries >= config.maxRetries) {
            observability.log(Level.WARNING, "entry ${entry.guid} reached max retries")
            repository.updateStatus(entry.guid, SmsRelayStatus.FAILED)
            cont()
        }
    }
}