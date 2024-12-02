package io.mrnateriver.smsproxy.server.framework

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.mrnateriver.smsproxy.controllers.MessagesProxyController.Companion.messagesProxyRoutes
import io.mrnateriver.smsproxy.controllers.ReceiversController.Companion.receiversRoutes
import io.mrnateriver.smsproxy.controllers.ReceiversRegisterController.Companion.receiversRegisterRoutes

fun Application.installApi() {
    install(ContentNegotiation) {
        json()
    }

    installErrorHandling()

    routing {
        val framework = DaggerFramework.create()
        val proxyController = framework.messagesProxyController()
        val receiversController = framework.receiversController()

        messagesProxyRoutes(proxyController)
        receiversRoutes(receiversController)
        receiversRegisterRoutes(receiversController)
    }
}
