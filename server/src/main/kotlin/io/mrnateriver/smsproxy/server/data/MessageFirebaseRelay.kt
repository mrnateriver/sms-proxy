package io.mrnateriver.smsproxy.server.data

import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import javax.inject.Inject
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService as MessageRelayServiceContract

class MessageFirebaseRelay @Inject constructor() : MessageRelayServiceContract {
    override suspend fun relay(entry: MessageEntry) {
        return
//        TODO("Not yet implemented")
        send("", entry.messageData.leftOrNull()!!)
    }

    // TODO: the signature of this method will have to change because server doesn't have access to pure MessageData
    private suspend fun send(fcmKey: String, message: MessageData) {
        TODO("Not yet implemented")
    }
}
