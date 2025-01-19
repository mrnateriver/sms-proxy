package io.mrnateriver.smsproxy.server.usecases

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import io.mrnateriver.smsproxy.shared.contracts.MessageStatsService as MessageStatsServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

private const val METRICS_NAME_PROCESSING_ERRORS = "processing_failures"
private const val METRICS_NAME_PROCESSING_SUCCESSES = "processing_successes"

class MessageStatsService @Inject constructor(
    private val observabilityService: ObservabilityServiceContract,
) : MessageStatsServiceContract {
    override fun incrementProcessingSuccesses() {
        CoroutineScope(Dispatchers.Default).launch {
            observabilityService.incrementCounter(METRICS_NAME_PROCESSING_SUCCESSES)
        }
    }

    override fun incrementProcessingErrors() {
        CoroutineScope(Dispatchers.Default).launch {
            observabilityService.incrementCounter(METRICS_NAME_PROCESSING_ERRORS)
        }
    }
}
