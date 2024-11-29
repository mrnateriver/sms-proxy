package io.mrnateriver.smsproxy.server.usecases

import io.ktor.server.plugins.BadRequestException
import io.mrnateriver.smsproxy.shared.models.MessageData
import kotlinx.datetime.Instant
import javax.inject.Inject
import io.mrnateriver.smsproxy.server.data.contracts.ReceiversRepository as ReceiversRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract

class MessageProxyUseCase @Inject constructor(
    private val messageProcessingService: MessageProcessingServiceContract,
    private val receiversRepository: ReceiversRepositoryContract,
) {
    suspend fun proxyMessage(request: ProxyMessageRequest) {
        if (!receiversRepository.doesReceiverExist(request.receiverKey)) {
            // TODO: proper validation error
            throw BadRequestException("Receiver does not exist")
        }

        // TODO: do the processing in the background
        messageProcessingService.process(request.toMessageData())
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
