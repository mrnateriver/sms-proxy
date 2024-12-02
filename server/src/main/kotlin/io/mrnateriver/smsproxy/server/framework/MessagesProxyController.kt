package io.mrnateriver.smsproxy.server.framework

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.Principal
import io.mrnateriver.smsproxy.controllers.TypedApplicationCall
import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.models.MessageProxyResponse
import io.mrnateriver.smsproxy.server.usecases.MessageProxyUseCase
import javax.inject.Inject
import io.mrnateriver.smsproxy.controllers.MessagesProxyController as MessagesProxyControllerContract

class MessagesProxyController @Inject constructor(
    private val messagesProxyUseCase: MessageProxyUseCase,
) : MessagesProxyControllerContract {
    override suspend fun messagesProxy(
        messageProxyRequest: MessageProxyRequest,
        principal: Principal,
        call: TypedApplicationCall<MessageProxyResponse>,
    ) {
        val guid = messagesProxyUseCase.proxyMessage(messageProxyRequest.toUseCaseRequest())
        call.respondTyped(HttpStatusCode.Created, MessageProxyResponse(externalId = guid.toString()))
    }
}

private fun MessageProxyRequest.toUseCaseRequest(): MessageProxyUseCase.ProxyMessageRequest {
    return MessageProxyUseCase.ProxyMessageRequest(
        receiverKey = receiverKey,
        sender = sender,
        message = message,
        receivedAt = receivedAt,
    )
}
