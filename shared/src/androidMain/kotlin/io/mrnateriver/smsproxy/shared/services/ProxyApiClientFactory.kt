package io.mrnateriver.smsproxy.shared.services

import io.mrnateriver.smsproxy.api.DefaultApi
import io.mrnateriver.smsproxy.auth.HttpBearerAuth
import io.mrnateriver.smsproxy.infrastructure.ApiClient
import io.mrnateriver.smsproxy.shared.BuildConfig
import io.mrnateriver.smsproxy.shared.contracts.LogLevel
import okhttp3.OkHttpClient
import okhttp3.internal.tls.OkHostnameVerifier
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import okhttp3.tls.decodeCertificatePem
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSession
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

typealias ProxyApi = DefaultApi

fun interface ProxyApiClientFactory {
    fun create(baseApiUrl: String): ProxyApi
}

fun createProxyApiClient(
    serverCertificatePem: String?,
    clientCertificatePem: String?,
    clientPrivateKeyPem: String?,
    observabilityService: ObservabilityServiceContract,
    baseApiUrl: String = BuildConfig.API_BASE_URL,
    apiKey: String = BuildConfig.API_KEY,
    loggingEnabled: Boolean = BuildConfig.DEBUG,
): ProxyApi {
    return ApiClient(
        baseApiUrl,
        createOkHttpClientBuilder(serverCertificatePem, clientCertificatePem, clientPrivateKeyPem)
    )
        .addAuthorization("Bearer", HttpBearerAuth(apiKey))
        .apply {
            if (loggingEnabled) {
                logger = { message -> observabilityService.log(LogLevel.DEBUG, message) }
            }
        }
        .createService(ProxyApi::class.java)
}

private fun createOkHttpClientBuilder(
    serverCertificatePem: String?,
    clientCertificatePem: String?,
    clientPrivateKeyPem: String?,
): OkHttpClient.Builder {

    return OkHttpClient.Builder()
        .readTimeout(BuildConfig.API_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .connectTimeout(BuildConfig.API_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .writeTimeout(BuildConfig.API_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .apply {
            val clientCertificates = createHandshakeCertificates(
                serverCertificatePem,
                clientCertificatePem,
                clientPrivateKeyPem
            )

            sslSocketFactory(clientCertificates.sslSocketFactory(), clientCertificates.trustManager)
            hostnameVerifier(::verifySelfSignedCertificateHost)
        }
}

internal fun createHandshakeCertificates(
    serverCertificatePem: String?,
    clientCertificatePem: String?,
    clientPrivateKeyPem: String?,
): HandshakeCertificates {
    val clientCertificatesBuilder = HandshakeCertificates.Builder()

    if (!serverCertificatePem.isNullOrBlank()) {
        val serverCertificate = serverCertificatePem.decodeCertificatePem()
        clientCertificatesBuilder.addTrustedCertificate(serverCertificate)
    }

    if (!clientCertificatePem.isNullOrBlank() && !clientPrivateKeyPem.isNullOrBlank()) {
        val heldCertificate =
            HeldCertificate.decode("$clientCertificatePem\n$clientPrivateKeyPem")
        clientCertificatesBuilder.heldCertificate(heldCertificate)
    }

    return clientCertificatesBuilder.build()
}

internal fun verifySelfSignedCertificateHost(hostname: String, session: SSLSession): Boolean {
    return OkHostnameVerifier.verify(BuildConfig.API_SERVER_CN, session)
}
