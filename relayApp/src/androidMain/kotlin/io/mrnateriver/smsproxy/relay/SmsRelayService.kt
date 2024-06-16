package io.mrnateriver.smsproxy.relay

import io.mrnateriver.smsproxy.shared.SmsEntry
import io.mrnateriver.smsproxy.shared.SmsRelayService as SmsRelayServiceContract

class SmsRelayService : SmsRelayServiceContract {
    override fun relay(entry: SmsEntry) {
        TODO("Send HTTP request") // look into shared API contracts with Ktor
    }
}