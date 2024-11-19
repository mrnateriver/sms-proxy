package io.mrnateriver.smsproxy

const val DEFAULT_SERVER_HOST = "127.0.0.1"
const val DEFAULT_SERVER_PORT = 4430

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

fun getTlsConfigurationFromEnv(): TlsConfiguration? {
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

fun getServerConfigurationFromEnv(defaultPort: Int? = null): ServerConfiguration {
    val host = System.getenv("SERVER_HOST") ?: DEFAULT_SERVER_HOST
    val port = System.getenv("SERVER_PORT")?.toInt() ?: defaultPort ?: DEFAULT_SERVER_PORT
    return ServerConfiguration(host, port)
}
