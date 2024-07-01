package io.mrnateriver.smsproxy.shared

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.withContext
import java.util.logging.Level

// TODO: tests

data class SmsProcessingConfig(
    val maxRetries: UShort,
)

class SmsProcessingService(
    private val repository: SmsRepository,
    private val relay: SmsRelayService,
    private val observability: ObservabilityService,
    private val config: SmsProcessingConfig = SmsProcessingConfig(3u),
) {
    suspend fun process(sms: SmsData): SmsEntry {
        return observability.runSpan("SmsProcessingService.process") {
            val entry = repository.insert(sms)
            processEntry(entry)

            entry
        }
    }

    suspend fun handleUnprocessedMessages(): Unit = coroutineScope {
        observability.runSpan("SmsProcessingService.handleUnprocessedMessages") {
            val entries = repository.getAll(SmsRelayStatus.ERROR, SmsRelayStatus.PENDING)
            observability.log(Level.INFO, "processing ${entries.size} entries")

            val sms = entries.map { async { processEntry(it) } }
            sms.joinAll()

            // TODO: return processing stats - success, errors and failures
        }
    }

    private suspend fun processEntry(entry: SmsEntry) = withContext(Dispatchers.IO) {
        observability.runSpan("SmsProcessingService.processEntry") {
            try {
                val retries = entry.sendRetries
                if (retries >= config.maxRetries) {
                    repository.updateStatus(entry.guid, SmsRelayStatus.FAILED)
                    return@runSpan
                }

                // TODO: logging
                // TODO: update number of retries **with the same call**

                repository.updateStatus(entry.guid, SmsRelayStatus.IN_PROGRESS)

                relay.relay(entry)

                repository.updateStatus(entry.guid, SmsRelayStatus.SUCCESS)
            } catch (e: Exception) {
                repository.updateStatus(entry.guid, SmsRelayStatus.ERROR)
            }
        }
    }
}