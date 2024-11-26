package io.mrnateriver.smsproxy.framework

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.Principal
import io.mrnateriver.smsproxy.models.ReceiverTransientParams
import io.mrnateriver.smsproxy.models.RegisterReceiverRequest
import javax.inject.Inject
import io.mrnateriver.smsproxy.controllers.ReceiversController as ReceiversControllerContract
import io.mrnateriver.smsproxy.controllers.ReceiversRegisterController as ReceiversRegisterControllerContract

class ReceiversController @Inject constructor() : ReceiversControllerContract, ReceiversRegisterControllerContract {
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
        TODO("Not yet implemented")
    }
}
