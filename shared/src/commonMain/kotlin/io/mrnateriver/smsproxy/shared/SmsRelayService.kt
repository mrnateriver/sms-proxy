package io.mrnateriver.smsproxy.shared

interface SmsRelayService {
    fun relay(entry: SmsEntry)
}