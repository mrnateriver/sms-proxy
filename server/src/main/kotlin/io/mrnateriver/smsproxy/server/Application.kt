package io.mrnateriver.smsproxy.server

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

val ktorDevMode = System.getProperty("io.ktor.development") == "true"

fun main() {
    initErrorHandling()

    val serverConfig = getServerConfigurationFromEnv()

    val server = embeddedServer(
        Netty,
        configure = {
            configureConnection(serverConfig)
        },
        module = {
            val telemetryServices = installTelemetry(serverConfig.telemetryConfig)
            installErrorHandling(telemetryServices)
            installAuth(serverConfig)
            installApi(serverConfig, telemetryServices)
        },
    )

    server.start(wait = true)
}
