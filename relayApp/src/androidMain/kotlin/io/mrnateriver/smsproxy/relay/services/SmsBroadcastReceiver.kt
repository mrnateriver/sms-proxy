package io.mrnateriver.smsproxy.relay.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import io.mrnateriver.smsproxy.shared.MessageProcessingService
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import io.mrnateriver.smsproxy.shared.models.MessageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import java.util.logging.Level
import javax.inject.Inject

@AndroidEntryPoint
class SmsBroadcastReceiver : BroadcastReceiver() {
    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var smsProcessingService: MessageProcessingService

    @Inject
    lateinit var statsService: MessageStatsService

    @Inject
    lateinit var observabilityService: ObservabilityService

    override fun onReceive(context: Context, intent: Intent) {
        observabilityService.log(Level.INFO, "Received intent broadcast: $intent")
        if (intent.action == SMS_RECEIVED_ACTION) {
            runBlocking(Dispatchers.IO) {
                observabilityService.runSpan("SmsBroadcastReceiver.onReceive") {
                    val msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                    if (msgs.isEmpty()) {
                        return@runSpan
                    }

                    val msgText = msgs.joinToString { it.displayMessageBody }
                    val msgSender = msgs.first().originatingAddress ?: ""

                    processMessage(msgSender, msgText)
                }
            }
        }
    }

    private suspend fun processMessage(sender: String, message: String) = coroutineScope {
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
            statsService.incrementProcessingFailures()

            observabilityService.log(
                Level.WARNING,
                "Failed to process message: $e\nScheduling background job to retry."
            )

            MessageProcessingWorker.schedule(context)
        } finally {
            statsService.triggerUpdate()
        }
    }
}
