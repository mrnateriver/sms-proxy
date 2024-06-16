package io.mrnateriver.smsproxy.relay

import android.provider.Telephony

class SmsInboxService {
    fun readTotalSmsCount(): Int {
        return 0
    }

    fun readAllSms(): List<Telephony.Sms> {
        return emptyList()
    }
}