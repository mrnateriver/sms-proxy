package io.mrnateriver.smsproxy.server

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
    val hashingSecret: String,
    val host: String,
    val port: Int,
    val tlsConfig: TlsConfiguration? = null,
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

fun getServerConfigurationFromEnv(): ServerConfiguration {
    val hashingSecret = System.getenv("HASHING_SECRET")
    require(!hashingSecret.isNullOrEmpty()) {
        "HASHING_SECRET must be set"
    }

    val host = System.getenv("SERVER_HOST") ?: DEFAULT_SERVER_HOST
    val port = System.getenv("SERVER_PORT")?.toInt() ?: DEFAULT_SERVER_PORT

    val tlsConfig = getTlsConfigurationFromEnv()

    return ServerConfiguration(hashingSecret = hashingSecret, host = host, port = port, tlsConfig = tlsConfig)
}
