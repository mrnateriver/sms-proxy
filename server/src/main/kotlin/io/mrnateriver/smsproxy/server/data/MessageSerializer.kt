package io.mrnateriver.smsproxy.server.data

import io.mrnateriver.smsproxy.shared.models.MessageData
import javax.inject.Inject
import io.mrnateriver.smsproxy.server.data.contracts.MessageEncrypter as MessageEncrypterContract
import io.mrnateriver.smsproxy.server.data.contracts.MessageSerializer as MessageSerializerContract
import io.mrnateriver.smsproxy.server.data.contracts.ReceiversRepository as ReceiversRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageSerializer @Inject constructor(
    private val receiversRepository: ReceiversRepositoryContract,
    private val messageEncrypter: MessageEncrypterContract,
    private val observabilityService: ObservabilityServiceContract,
) : MessageSerializerContract {
    override suspend fun serialize(message: MessageData): ByteArray {
        return observabilityService.runSpan("MessageSerializer.serialize") {
            require(message.receiverKey != null) { "Message must have a receiver key to be encrypted" }

            val receiver = receiversRepository.findReceiverByKey(message.receiverKey!!)
            checkNotNull(receiver) { "Message receiver not found" }

            messageEncrypter.encrypt(receiver.publicKey, message)
        }
    }
}
