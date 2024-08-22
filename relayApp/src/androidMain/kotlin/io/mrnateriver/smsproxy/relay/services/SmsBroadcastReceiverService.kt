package io.mrnateriver.smsproxy.relay.services

import android.content.Context
import android.content.Intent
import io.mrnateriver.smsproxy.shared.models.MessageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import java.util.logging.Level
import javax.inject.Inject
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class SmsBroadcastReceiverService @Inject constructor(
    private var smsProcessingService: MessageProcessingServiceContract,
    private var statsService: MessageStatsServiceContract,
    private var observabilityService: ObservabilityServiceContract,
    private var workerService: MessageProcessingWorkerServiceContract,
    private var smsIntentParserService: SmsIntentParserServiceContract,
) {
    fun handleIncomingMessagesIntent(context: Context, intent: Intent) {
        runBlocking(Dispatchers.IO) {
            observabilityService.runSpan("SmsBroadcastReceiverService.handleIncomingMessagesIntent") {
                val msgs = smsIntentParserService.getMessagesFromIntent(intent)
                if (msgs.isEmpty()) {
                    return@runSpan
                }

                val msgText = msgs.joinToString("") { it.displayMessageBody }
                val msgSender = msgs.first().originatingAddress ?: ""

                processMessage(context, msgSender, msgText)
            }
        }
    }

    private suspend fun processMessage(context: Context, sender: String, message: String) =
        coroutineScope {
            try {
                smsProcessingService.process(
                    MessageData(
                        sender = sender,
                        message = message,

                        // We have to use our own record creation timestamp, because timestampMillis in
                        // SMS messages is parsed from the PDU and reported by the network, which can
                        // operate in a different time zone
                        receivedAt = Clock.System.now(),
                    )
                )
            } catch (e: Exception) {
                statsService.incrementProcessingErrors()

                observabilityService.log(
                    Level.WARNING,
                    "Failed to process message: $e\nScheduling background job to retry."
                )

                workerService.scheduleBackgroundWork(context)
            } finally {
                statsService.triggerUpdate()
            }
        }
}
