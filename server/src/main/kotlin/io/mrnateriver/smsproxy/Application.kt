package io.mrnateriver.smsproxy

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.BearerTokenCredential
import io.ktor.server.auth.Principal
import io.ktor.server.auth.bearer
import io.ktor.server.auth.principal
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.mrnateriver.smsproxy.controllers.MessagesProxyController
import io.mrnateriver.smsproxy.controllers.MessagesProxyController.Companion.messagesProxyRoutes
import io.mrnateriver.smsproxy.controllers.TypedApplicationCall
import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.models.MessageProxyResponse
import kotlin.random.Random

fun main() {
    val serverConfig = getServerConfigurationFromEnv()
    val tlsConfig = getTlsConfigurationFromEnv()

    val server = embeddedServer(
        Netty,
        configure = {
            if (tlsConfig != null) {
                tlsConnector(serverConfig, tlsConfig)
            } else {
                connector {
                    host = serverConfig.host
                    port = serverConfig.port
                }
            }
        },
        module = {
            install(Authentication) {
                bearer("BearerAuth") {
                    authenticate { credentials: BearerTokenCredential ->
                        // TODO: check API token
                        principal()
                    }
                }
            }
            messageProxyApi()
        },
    )

    server.start(wait = true)
}

fun Application.messageProxyApi() {
    routing {
        messagesProxyRoutes(object : MessagesProxyController {
            override suspend fun messagesProxy(
                messageProxyRequest: MessageProxyRequest,
                principal: Principal,
                call: TypedApplicationCall<MessageProxyResponse>,
            ) {
                println("Request: $messageProxyRequest")
                call.respondTyped(HttpStatusCode.NoContent, MessageProxyResponse("test"))
            }
        })

        val rnd = Random(seed = 42)
        get("/hello") {
            call.respondText("Ktor: ${rnd.nextInt()}")
        }
    }
}
