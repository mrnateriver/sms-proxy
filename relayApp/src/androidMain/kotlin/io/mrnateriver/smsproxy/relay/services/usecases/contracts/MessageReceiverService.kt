package io.mrnateriver.smsproxy.relay.services.usecases.contracts

interface MessageReceiverService {
    fun handleIncomingMessage(sender: String, message: String)
}