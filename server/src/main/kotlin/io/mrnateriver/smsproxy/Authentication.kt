package io.mrnateriver.smsproxy

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.BearerTokenCredential
import io.ktor.server.auth.Principal
import io.ktor.server.auth.bearer

fun Application.installAuth() {
    val packageName = ::main.javaClass.packageName
    val apiKey = System.getProperty("$packageName.apiKey")
    require(apiKey != null) {
        "apiKey must be provided as a system property $packageName.apiKey"
    }

    install(Authentication) {
        bearer("BearerAuth") {
            authenticate { credentials: BearerTokenCredential ->
                if (credentials.token == apiKey) {
                    ApiKeyPrincipal(apiKey)
                } else {
                    null
                }
            }
        }
    }
}

private data class ApiKeyPrincipal(val token: String) : Principal
