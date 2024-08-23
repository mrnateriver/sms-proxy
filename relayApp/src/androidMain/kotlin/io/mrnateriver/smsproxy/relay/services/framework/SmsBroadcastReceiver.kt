package io.mrnateriver.smsproxy.relay.services.framework

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageReceiverService as MessageReceiverServiceContract

@AndroidEntryPoint
class SmsBroadcastReceiver : BroadcastReceiver() {
    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var receiverService: MessageReceiverServiceContract

    @Inject
    lateinit var intentParserService: SmsIntentParserService

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SMS_RECEIVED_ACTION) {
            val parsedMessage = intentParserService.getMessagesFromIntent(intent) ?: return
            receiverService.handleIncomingMessage(parsedMessage.sender, parsedMessage.message)
        }
    }
}
