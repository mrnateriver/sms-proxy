package io.mrnateriver.smsproxy.relay.services.usecases

import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageBackgroundProcessingService.MessageBackgroundProcessingResult
import io.mrnateriver.smsproxy.shared.contracts.LogLevel
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageBackgroundProcessingService as MessageBackgroundProcessingServiceContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageStatsService as MessageStatsServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageBackgroundProcessingService @Inject constructor(
    private val processingService: MessageProcessingServiceContract,
    private val statsService: MessageStatsServiceContract,
    private val observabilityService: ObservabilityServiceContract,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MessageBackgroundProcessingServiceContract {
    override suspend fun handleUnprocessedMessages(): MessageBackgroundProcessingResult = withContext(dispatcher) {
        observabilityService.runSpan("MessageBackgroundProcessingService.handleUnprocessedMessages") {
            val results = processingService.handleUnprocessedMessages().map { it.sendStatus }.toList()
            val result = when {
                results.any {
                    it == MessageRelayStatus.ERROR || it == MessageRelayStatus.IN_PROGRESS
                } -> MessageBackgroundProcessingResult.RETRY

                results.any {
                    it == MessageRelayStatus.SUCCESS
                    // This would mean the rest have FAILURE status, and we don't want to retry them
                } -> MessageBackgroundProcessingResult.SUCCESS

                results.isNotEmpty() -> MessageBackgroundProcessingResult.FAILURE
                else -> MessageBackgroundProcessingResult.SUCCESS
            }

            repeat(results.count { it == MessageRelayStatus.ERROR }) {
                statsService.incrementProcessingErrors()
            }

            statsService.triggerUpdate()

            observabilityService.log(
                LogLevel.DEBUG,
                "Processed ${results.size} messages: $result",
            )
            result
        }
    }
}
