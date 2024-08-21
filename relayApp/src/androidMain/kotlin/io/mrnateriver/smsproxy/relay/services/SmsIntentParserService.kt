package io.mrnateriver.smsproxy.relay.services

import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage

interface SmsIntentParserServiceContract {
    fun getMessagesFromIntent(intent: Intent): Array<SmsMessage>
}

class SmsIntentParserService : SmsIntentParserServiceContract {
    override fun getMessagesFromIntent(intent: Intent): Array<SmsMessage> {
        return Telephony.Sms.Intents.getMessagesFromIntent(intent)
    }
}
