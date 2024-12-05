package io.mrnateriver.smsproxy.server

import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.connector

fun ApplicationEngine.Configuration.configureConnection(serverConfig: ServerConfiguration) {
    if (serverConfig.tlsConfig != null) {
        tlsConnector(serverConfig)
    } else {
        connector {
            host = serverConfig.host
            port = serverConfig.port
        }
    }
}
