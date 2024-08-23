package io.mrnateriver.smsproxy.relay.services.framework

import android.content.Intent
import android.provider.Telephony

class SmsIntentParserService {
    fun getMessagesFromIntent(intent: Intent): ParsedSmsMessage? {
        val msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (msgs.isEmpty()) {
            return null
        }

        val msgText = msgs.joinToString("") { it.displayMessageBody }
        val msgSender = msgs.first().originatingAddress ?: ""

        return ParsedSmsMessage(msgSender, msgText)
    }

    data class ParsedSmsMessage(
        val sender: String,
        val message: String,
    )
}
