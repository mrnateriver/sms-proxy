package io.mrnateriver.smsproxy.server

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.mrnateriver.smsproxy.controllers.MessagesProxyController.Companion.messagesProxyRoutes
import io.mrnateriver.smsproxy.controllers.ReceiversController.Companion.receiversRoutes
import io.mrnateriver.smsproxy.controllers.ReceiversRegisterController.Companion.receiversRegisterRoutes
import io.mrnateriver.smsproxy.server.framework.DaggerFramework

fun Application.installApi(serverConfig: ServerConfiguration) {
    install(ContentNegotiation) {
        json()
    }

    installErrorHandling()

    val telemetryServices = installTelemetry(serverConfig.telemetryConfig)
    routing {
        val framework = DaggerFramework.builder()
            .logger(log)
            .hashingSecret(serverConfig.hashingSecret)
            .serverConfig(serverConfig)
            .telemetryServices(telemetryServices)
            .build()
        val proxyController = framework.messagesProxyController()
        val receiversController = framework.receiversController()

        messagesProxyRoutes(proxyController)
        receiversRoutes(receiversController)
        receiversRegisterRoutes(receiversController)
    }
}
