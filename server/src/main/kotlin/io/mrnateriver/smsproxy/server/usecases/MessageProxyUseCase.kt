package io.mrnateriver.smsproxy.server.usecases

import io.mrnateriver.smsproxy.server.entities.exceptions.ValidationException
import io.mrnateriver.smsproxy.shared.models.MessageData
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.UUID
import javax.inject.Inject
import io.mrnateriver.smsproxy.server.data.contracts.ReceiversRepository as ReceiversRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageProxyUseCase @Inject constructor(
    private val messageProcessingService: MessageProcessingServiceContract,
    private val receiversRepository: ReceiversRepositoryContract,
    private val observabilityService: ObservabilityServiceContract,
) {
    suspend fun proxyMessage(request: ProxyMessageRequest): UUID {
        validateProxyRequest(request)
        return messageProcessingService.process(request.toMessageData()).guid
    }

    @Throws(ValidationException::class)
    private suspend fun validateProxyRequest(request: ProxyMessageRequest) {
        observabilityService.runSpan("MessageProxyUseCase.validateProxyRequest") {
            val errors = mutableMapOf<String, List<String>>()

            if (request.receiverKey.isBlank()) {
                errors["receiverKey"] = listOf("Receiver key must not be blank")
            } else if (!receiversRepository.doesReceiverExist(request.receiverKey)) {
                // This is prone to brute forcing keys, but considering the security model which only relies on secrecy
                // of static keys and the fact that the whole system simply proxies SMS, which can be sent to any
                // number, the source of the message must not be trusted anyway, and thus brute forcing keys is not a
                // threat in itself
                errors["receiverKey"] = listOf("Receiver with the specified key does not exist")
            }

            if (request.sender.isBlank()) {
                errors["sender"] = listOf("Sender must not be blank")
            }

            if (request.message.isBlank()) {
                errors["message"] = listOf("Message must not be blank")
            }

            if (request.receivedAt > Clock.System.now()) {
                errors["receivedAt"] = listOf("Received at must not be in the future")
            }

            if (errors.isNotEmpty()) {
                throw ValidationException(errors)
            }
        }
    }

    data class ProxyMessageRequest(
        val receiverKey: String,
        val sender: String,
        val message: String,
        val receivedAt: Instant,
    ) {
        fun toMessageData(): MessageData {
            return MessageData(
                receiverKey = receiverKey,
                sender = sender,
                message = message,
                receivedAt = receivedAt,
            )
        }
    }
}
