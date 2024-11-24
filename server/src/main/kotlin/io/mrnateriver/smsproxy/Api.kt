package io.mrnateriver.smsproxy

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.auth.Principal
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.mrnateriver.smsproxy.controllers.MessagesProxyController
import io.mrnateriver.smsproxy.controllers.MessagesProxyController.Companion.messagesProxyRoutes
import io.mrnateriver.smsproxy.controllers.ReceiversController
import io.mrnateriver.smsproxy.controllers.ReceiversController.Companion.receiversRoutes
import io.mrnateriver.smsproxy.controllers.ReceiversRegisterController
import io.mrnateriver.smsproxy.controllers.ReceiversRegisterController.Companion.receiversRegisterRoutes
import io.mrnateriver.smsproxy.controllers.TypedApplicationCall
import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.models.MessageProxyResponse
import io.mrnateriver.smsproxy.models.ReceiverTransientParams
import io.mrnateriver.smsproxy.models.RegisterReceiverRequest
import kotlin.random.Random

fun Application.installApi() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        messagesProxyRoutes(object : MessagesProxyController {
            override suspend fun messagesProxy(
                messageProxyRequest: MessageProxyRequest,
                principal: Principal,
                call: TypedApplicationCall<MessageProxyResponse>,
            ) {
                println("Request: $messageProxyRequest")
                call.respondTyped(HttpStatusCode.Created, MessageProxyResponse("test"))
            }
        })

        receiversRoutes(object : ReceiversController {
            override suspend fun receiversUpdate(
                receiverKey: String,
                receiverTransientParams: ReceiverTransientParams,
                principal: Principal,
                call: ApplicationCall,
            ) {
                println("Request: $receiverTransientParams")
                call.response.status(HttpStatusCode.NoContent)
            }
        })

        receiversRegisterRoutes(object : ReceiversRegisterController {
            override suspend fun receiversRegister(
                registerReceiverRequest: RegisterReceiverRequest,
                principal: Principal,
                call: ApplicationCall,
            ) {
                println("Request: $registerReceiverRequest")
                call.response.status(HttpStatusCode.NoContent)
            }
        })

        authenticate("BearerAuth", optional = false) {
            val rnd = Random(seed = 42)
            get("/hello") {
                call.respondText("Ktor: ${rnd.nextInt()}")
            }
        }
    }
}
