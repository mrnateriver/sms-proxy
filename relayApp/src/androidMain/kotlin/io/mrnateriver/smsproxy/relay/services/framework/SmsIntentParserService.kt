package io.mrnateriver.smsproxy.relay.services.framework

import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage

class SmsIntentParserService(
    private val intentParser: (Intent) -> Array<SmsMessage>? = Telephony.Sms.Intents::getMessagesFromIntent,
) {
    fun getMessagesFromIntent(intent: Intent): ParsedSmsMessage? {
        val msgs = intentParser(intent)
        if (msgs.isNullOrEmpty()) {
            return null
        }

        val msgText = msgs.joinToString("") { it.displayMessageBody }
        val msgSender = msgs.first().originatingAddress.orEmpty()

        return ParsedSmsMessage(msgSender, msgText)
    }

    data class ParsedSmsMessage(
        val sender: String,
        val message: String,
    )
}
