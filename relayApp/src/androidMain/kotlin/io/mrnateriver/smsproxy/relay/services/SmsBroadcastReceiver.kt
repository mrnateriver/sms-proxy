package io.mrnateriver.smsproxy.relay.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import android.telephony.SmsMessage
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import io.mrnateriver.smsproxy.shared.MessageProcessingService
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import io.mrnateriver.smsproxy.shared.models.MessageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
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
    lateinit var statsService: MessageStatsService

    @Inject
    lateinit var observabilityService: ObservabilityService

    override fun onReceive(context: Context, intent: Intent) {
        observabilityService.log(Level.INFO, "Received intent broadcast: $intent")
        if (intent.action == SMS_RECEIVED_ACTION) {
            runBlocking(Dispatchers.IO) {
                observabilityService.runSpan("SmsBroadcastReceiver.onReceive") {
                    Telephony.Sms.Intents.getMessagesFromIntent(intent)
                        .map { message -> launch { processMessage(message) } }
                        .joinAll()
                }
            }
        }
    }

    private suspend fun processMessage(message: SmsMessage) = coroutineScope {
        try {
            smsProcessingService.process(
                MessageData(
                    sender = message.displayOriginatingAddress ?: "",
                    message = message.displayMessageBody,
                    receivedAt = Instant.fromEpochMilliseconds(message.timestampMillis),
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
