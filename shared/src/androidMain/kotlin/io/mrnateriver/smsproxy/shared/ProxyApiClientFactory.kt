package io.mrnateriver.smsproxy.shared

import io.mrnateriver.smsproxy.api.DefaultApi
import io.mrnateriver.smsproxy.auth.HttpBearerAuth
import io.mrnateriver.smsproxy.infrastructure.ApiClient
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import okhttp3.OkHttpClient
import okhttp3.internal.tls.OkHostnameVerifier
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.HeldCertificate
import okhttp3.tls.decodeCertificatePem
import java.util.concurrent.TimeUnit
import java.util.logging.Level

typealias ProxyApi = DefaultApi

fun interface ProxyApiClientFactory {
    fun create(baseApiUrl: String): ProxyApi
}

fun createProxyApiClient(
    serverCertificatePem: String?,
    clientCertificatePem: String?,
    clientPrivateKeyPem: String?,
    observabilityService: ObservabilityService,
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
                logger = { message -> observabilityService.log(Level.FINEST, message) }
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

            val clientCertificates = clientCertificatesBuilder.build()
            sslSocketFactory(clientCertificates.sslSocketFactory(), clientCertificates.trustManager)
            hostnameVerifier { _, session ->
                OkHostnameVerifier.verify(
                    BuildConfig.API_SERVER_CN,
                    session
                )
            }
        }
}