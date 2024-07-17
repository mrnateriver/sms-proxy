package io.mrnateriver.smsproxy.relay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import android.telephony.SmsMessage
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import io.mrnateriver.smsproxy.relay.services.MessageProcessingWorker
import io.mrnateriver.smsproxy.shared.MessageProcessingService
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import io.mrnateriver.smsproxy.shared.models.MessageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
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
    lateinit var observabilityService: ObservabilityService

    override fun onReceive(context: Context, intent: Intent) {
        observabilityService.log(Level.INFO, "Received intent broadcast: $intent")
        if (intent.action == SMS_RECEIVED_ACTION) {
            runBlocking {
                observabilityService.runSpan("SmsBroadcastReceiver.onReceive") {
                    for (message in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                        processMessage(message)
                    }
                }
            }
        }
    }

    private fun processMessage(message: SmsMessage) {
        runBlocking(Dispatchers.IO) {
            try {
                smsProcessingService.process(
                    MessageData(
                        sender = message.displayOriginatingAddress ?: "",
                        message = message.displayMessageBody,
                        receivedAt = Instant.fromEpochMilliseconds(message.timestampMillis),
                    )
                )
            } catch (e: Exception) {
                // TODO: record error in stats service

                observabilityService.log(
                    Level.WARNING,
                    "Failed to process message: $e\nScheduling background job to retry."
                )

                MessageProcessingWorker.schedule(context)
            }
        }
    }
}
