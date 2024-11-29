package io.mrnateriver.smsproxy.server

import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.connector

fun ApplicationEngine.Configuration.configureConnection() {
    val tlsConfig = getTlsConfigurationFromEnv()
    val serverConfig = getServerConfigurationFromEnv()

    if (tlsConfig != null) {
        tlsConnector(serverConfig, tlsConfig)
    } else {
        connector {
            host = serverConfig.host
            port = serverConfig.port
        }
    }
}

private const val DEFAULT_SERVER_HOST = "127.0.0.1"
private const val DEFAULT_SERVER_PORT = 4430

data class TlsConfiguration(
    val keyStorePassword: String,
    val keyPassword: String,
    val keyAlias: String,
    val keyPath: String,
    val clientsKeysPath: String,
)

data class ServerConfiguration(
    val host: String,
    val port: Int,
)

private fun getTlsConfigurationFromEnv(): TlsConfiguration? {
    val path = System.getenv("CERT_JKS_PATH")
    val clientsKeysPath = System.getenv("CERT_CLIENTS_PATH")
    if (path.isNullOrEmpty() || clientsKeysPath.isNullOrEmpty()) {
        return null
    }

    val keyStorePassword = System.getenv("CERT_KEY_STORE_PASSWORD")
    val keyPassword = System.getenv("CERT_KEY_PASSWORD")

    require(!keyPassword.isNullOrEmpty()) {
        "CERT_KEY_PASSWORD must be set if JKS path is provided"
    }
    require(!keyStorePassword.isNullOrEmpty()) {
        "CERT_KEY_STORE_PASSWORD must be set if JKS path is provided"
    }

    return TlsConfiguration(
        keyAlias = System.getenv("CERT_KEY_ALIAS") ?: "serverKey",
        keyStorePassword = keyStorePassword,
        keyPassword = keyPassword,
        clientsKeysPath = clientsKeysPath,
        keyPath = path,
    )
}

private fun getServerConfigurationFromEnv(): ServerConfiguration {
    val host = System.getenv("SERVER_HOST") ?: DEFAULT_SERVER_HOST
    val port = System.getenv("SERVER_PORT")?.toInt() ?: DEFAULT_SERVER_PORT
    return ServerConfiguration(host, port)
}
