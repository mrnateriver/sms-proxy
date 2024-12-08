package io.mrnateriver.smsproxy.server

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.BearerTokenCredential
import io.ktor.server.auth.Principal
import io.ktor.server.auth.bearer

fun Application.installAuth(serverConfig: ServerConfiguration) {
    install(Authentication) {
        bearer("BearerAuth") {
            authenticate { credentials: BearerTokenCredential ->
                if (credentials.token == serverConfig.apiKey) {
                    ApiKeyPrincipal(serverConfig.apiKey)
                } else {
                    null
                }
            }
        }
    }
}

private data class ApiKeyPrincipal(val token: String) : Principal
