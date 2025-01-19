package io.mrnateriver.smsproxy.relay.services.usecases

import io.mrnateriver.smsproxy.shared.contracts.LogLevel
import io.mrnateriver.smsproxy.shared.models.MessageData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import javax.inject.Inject
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageProcessingScheduler as MessageProcessingSchedulerContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageReceiverService as MessageReceiverServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageReceiverService @Inject constructor(
    private val smsProcessingService: MessageProcessingServiceContract,
    private val observabilityService: ObservabilityServiceContract,
    private val workerScheduler: MessageProcessingSchedulerContract,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MessageReceiverServiceContract {
    override fun handleIncomingMessage(sender: String, message: String) {
        runBlocking(dispatcher) {
            observabilityService.runSpan("SmsBroadcastReceiverService.handleIncomingMessage") {
                try {
                    smsProcessingService.process(
                        MessageData(
                            sender = sender,
                            message = message,

                            // We have to use our own record creation timestamp, because timestampMillis in
                            // SMS messages is parsed from the PDU and reported by the network, which can
                            // operate in a different time zone
                            receivedAt = Clock.System.now(),
                        ),
                    )
                } catch (e: Exception) {
                    observabilityService.log(
                        LogLevel.WARNING,
                        "Failed to process message: $e\nScheduling background job to retry.",
                    )

                    workerScheduler.scheduleBackgroundMessageProcessing()
                }
            }
        }
    }
}
