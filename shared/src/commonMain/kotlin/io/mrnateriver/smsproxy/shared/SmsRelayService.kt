package io.mrnateriver.smsproxy.shared

interface SmsRelayService {
    suspend fun relay(entry: SmsEntry)
}