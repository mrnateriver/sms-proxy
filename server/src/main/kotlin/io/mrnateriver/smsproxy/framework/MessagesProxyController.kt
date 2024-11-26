package io.mrnateriver.smsproxy.framework

import io.ktor.server.auth.Principal
import io.mrnateriver.smsproxy.controllers.TypedApplicationCall
import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.models.MessageProxyResponse
import io.mrnateriver.smsproxy.usecases.MessageProxyUseCase
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
        messagesProxyUseCase.proxyMessage()
        TODO("Not yet implemented")
    }
}
