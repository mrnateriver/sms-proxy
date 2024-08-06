package io.mrnateriver.smsproxy.shared

import io.mrnateriver.smsproxy.api.DefaultApi
import io.mrnateriver.smsproxy.auth.HttpBearerAuth
import io.mrnateriver.smsproxy.infrastructure.ApiClient
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import java.util.logging.Level

typealias ProxyApi = DefaultApi

fun interface ProxyApiClientFactory {
    fun create(baseApiUrl: String): ProxyApi
}

fun createProxyApiClient(
    observabilityService: ObservabilityService,
    baseApiUrl: String = BuildConfig.API_BASE_URL,
    apiKey: String = BuildConfig.API_KEY,
    loggingEnabled: Boolean = BuildConfig.DEBUG,
): ProxyApi {
    return ApiClient(baseApiUrl, createOkHttpClientBuilder())
        .addAuthorization("Bearer", HttpBearerAuth(apiKey))
        .apply {
            if (loggingEnabled) {
                logger = { message -> observabilityService.log(Level.FINEST, message) }
            }
        }
        .createService(ProxyApi::class.java)
}

private fun createOkHttpClientBuilder(): OkHttpClient.Builder =
    OkHttpClient.Builder()
        .readTimeout(BuildConfig.API_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .connectTimeout(BuildConfig.API_TIMEOUT_MS, TimeUnit.MILLISECONDS)
        .writeTimeout(BuildConfig.API_TIMEOUT_MS, TimeUnit.MILLISECONDS)