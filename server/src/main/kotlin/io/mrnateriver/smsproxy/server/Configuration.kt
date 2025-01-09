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

data class DatabaseConfiguration(
    val url: String,
    val user: String,
    val password: String,
)

data class TelemetryConfiguration(
    val metricsHttpPort: Int,
    val otlpGrpcEndpoint: String?,
    val otlpServiceName: String,
)

data class ServerConfiguration(
    val apiKey: String,
    val hashingSecret: String,
    val host: String,
    val port: Int,
    val db: DatabaseConfiguration,
    val telemetryConfig: TelemetryConfiguration,
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

private fun getDatabaseConfigurationFromEnv(): DatabaseConfiguration {
    val jdbcUrl = System.getenv("DB_JDBC_URI")
    val dbUsername = System.getenv("DB_USER")
    val dbPassword = System.getenv("DB_PASSWORD")
    require(!jdbcUrl.isNullOrEmpty()) { "DB_JDBC_URI must be set" }
    require(!dbUsername.isNullOrEmpty()) { "DB_USER must be set" }
    require(!dbPassword.isNullOrEmpty()) { "DB_PASSWORD must be set" }

    return DatabaseConfiguration(url = jdbcUrl, user = dbUsername, password = dbPassword)
}

private fun getTelemetryConfigurationFromEnv(): TelemetryConfiguration {
    val otlpTracingGrpcUrl = System.getenv("OTLP_TRACING_GRPC_URL")
    val metricsHttpPort = System.getenv("METRICS_HTTP_PORT")?.toIntOrNull() ?: 4000
    val serviceName = System.getenv("OTLP_SERVICE_NAME") ?: "sms-proxy"

    return TelemetryConfiguration(
        metricsHttpPort = metricsHttpPort,
        otlpGrpcEndpoint = otlpTracingGrpcUrl,
        otlpServiceName = serviceName,
    )
}

fun getServerConfigurationFromEnv(): ServerConfiguration {
    val hashingSecret = System.getenv("HASHING_SECRET")
    require(!hashingSecret.isNullOrEmpty()) {
        "HASHING_SECRET must be set"
    }

    // API key is passed as a system property in addition to an env var because it is synced with Android apps at
    // build time and embedded in the server's build artifacts in dev mode
    val packageName = ::main.javaClass.packageName
    val apiKey = System.getenv("API_KEY") ?: System.getProperty("$packageName.apiKey")
    require(apiKey != null) {
        "API_KEY environment variable or a system property $packageName.apiKey must be set"
    }

    val host = System.getenv("SERVER_HOST") ?: DEFAULT_SERVER_HOST
    val port = System.getenv("SERVER_PORT")?.toInt() ?: DEFAULT_SERVER_PORT

    val tlsConfig = getTlsConfigurationFromEnv()
    val dbConfig = getDatabaseConfigurationFromEnv()
    val telemetryConfig = getTelemetryConfigurationFromEnv()

    return ServerConfiguration(
        host = host,
        port = port,
        db = dbConfig,
        apiKey = apiKey,
        tlsConfig = tlsConfig,
        hashingSecret = hashingSecret,
        telemetryConfig = telemetryConfig,
    )
}

