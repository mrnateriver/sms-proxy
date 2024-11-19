package io.mrnateriver.smsproxy

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.BearerTokenCredential
import io.ktor.server.auth.Principal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.bearer
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.mrnateriver.smsproxy.controllers.MessagesProxyController
import io.mrnateriver.smsproxy.controllers.MessagesProxyController.Companion.messagesProxyRoutes
import io.mrnateriver.smsproxy.controllers.TypedApplicationCall
import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.models.MessageProxyResponse
import kotlin.random.Random

const val DEFAULT_TLS_PORT = 4430
const val DEFAULT_PLAINTEXT_PORT = 8080

fun main() {
    val tlsConfig = getTlsConfigurationFromEnv()
    val serverConfig = getServerConfigurationFromEnv(tlsConfig?.let { DEFAULT_TLS_PORT } ?: DEFAULT_PLAINTEXT_PORT)

    val packageName = ::main.javaClass.packageName
    val apiKey = System.getProperty("$packageName.apiKey")
    require(apiKey != null) {
        "apiKey must be provided as a system property $packageName.apiKey"
    }

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
            install(ContentNegotiation) {
                json()
            }

            // TODO: move the whole auth module
            install(Authentication) {
                bearer("BearerAuth") {
                    authenticate { credentials: BearerTokenCredential ->
                        data class ApiKeyPrincipal(val token: String) : Principal
                        if (credentials.token == apiKey) {
                            ApiKeyPrincipal(apiKey)
                        } else {
                            null
                        }
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
                call.respondTyped(HttpStatusCode.Created, MessageProxyResponse("test"))
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
