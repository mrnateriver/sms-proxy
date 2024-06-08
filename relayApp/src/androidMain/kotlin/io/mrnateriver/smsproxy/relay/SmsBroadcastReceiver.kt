package io.mrnateriver.smsproxy.relay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.provider.Telephony.Sms.Intents.SMS_RECEIVED_ACTION
import android.util.Log

class SmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SMS_RECEIVED_ACTION) {
            // TODO: save the sms to local storage synchronously
            // TODO: spawn background job that would send the SMS to the server

            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            val smsMessageBuilder = StringBuilder()

            for (message in smsMessages) {
                val phoneNumber = message.displayOriginatingAddress
                smsMessageBuilder.append("From: $phoneNumber\n")
                smsMessageBuilder.append(message.displayMessageBody)
                smsMessageBuilder.append("\n")
            }

            Log.i("SMSReceiver", smsMessageBuilder.toString())
        }
    }
}
