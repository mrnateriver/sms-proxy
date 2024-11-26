package io.mrnateriver.smsproxy

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.mrnateriver.smsproxy.controllers.MessagesProxyController.Companion.messagesProxyRoutes
import io.mrnateriver.smsproxy.controllers.ReceiversController.Companion.receiversRoutes
import io.mrnateriver.smsproxy.controllers.ReceiversRegisterController.Companion.receiversRegisterRoutes
import io.mrnateriver.smsproxy.framework.DaggerFramework

fun Application.installApi() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        val framework = DaggerFramework.create()
        val proxyController = framework.messagesProxyController()
        val receiversController = framework.receiversController()

        messagesProxyRoutes(proxyController)
        receiversRoutes(receiversController)
        receiversRegisterRoutes(receiversController)
    }
}
