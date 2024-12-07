package io.mrnateriver.smsproxy.server.framework

import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.callid.generate
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.mrnateriver.smsproxy.controllers.MessagesProxyController.Companion.messagesProxyRoutes
import io.mrnateriver.smsproxy.controllers.ReceiversController.Companion.receiversRoutes
import io.mrnateriver.smsproxy.controllers.ReceiversRegisterController.Companion.receiversRegisterRoutes
import io.mrnateriver.smsproxy.server.ServerConfiguration
import org.slf4j.event.Level

fun Application.installApi(serverConfig: ServerConfiguration) {
    install(ContentNegotiation) {
        json()
    }

    installErrorHandling()

    install(CallId) {
        retrieveFromHeader(HttpHeaders.XRequestId)
        replyToHeader(HttpHeaders.XRequestId)
        generate(length = 32, dictionary = "0123456789abcdef")
    }

    install(CallLogging) {
        callIdMdc(HttpHeaders.XRequestId)
        level = Level.DEBUG
    }

    routing {
        val framework = DaggerFramework.builder().logger(log).hashingSecret(serverConfig.hashingSecret).build()
        val proxyController = framework.messagesProxyController()
        val receiversController = framework.receiversController()

        messagesProxyRoutes(proxyController)
        receiversRoutes(receiversController)
        receiversRegisterRoutes(receiversController)
    }
}
