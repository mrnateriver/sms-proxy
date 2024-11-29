package io.mrnateriver.smsproxy.server.framework

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.Principal
import io.mrnateriver.smsproxy.models.ReceiverTransientParams
import io.mrnateriver.smsproxy.models.RegisterReceiverRequest
import io.mrnateriver.smsproxy.server.usecases.ReceiversUseCase
import javax.inject.Inject
import io.mrnateriver.smsproxy.controllers.ReceiversController as ReceiversControllerContract
import io.mrnateriver.smsproxy.controllers.ReceiversRegisterController as ReceiversRegisterControllerContract

class ReceiversController @Inject constructor(private val receiversUseCase: ReceiversUseCase) :
    ReceiversControllerContract, ReceiversRegisterControllerContract {
    override suspend fun receiversUpdate(
        receiverKey: String,
        receiverTransientParams: ReceiverTransientParams,
        principal: Principal,
        call: ApplicationCall,
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun receiversRegister(
        registerReceiverRequest: RegisterReceiverRequest,
        principal: Principal,
        call: ApplicationCall,
    ) {
        receiversUseCase.registerReceiver(registerReceiverRequest.toUseCaseRequest())
        call.response.status(HttpStatusCode.Created)
    }
}

private fun RegisterReceiverRequest.toUseCaseRequest(): ReceiversUseCase.RegisterReceiverRequest {
    return ReceiversUseCase.RegisterReceiverRequest(
        notificationsId = notificationsId,
        receiverKey = receiverKey,
        publicKey = publicKey,
    )
}
