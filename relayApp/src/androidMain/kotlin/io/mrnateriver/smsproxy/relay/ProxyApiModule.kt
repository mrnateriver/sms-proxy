package io.mrnateriver.smsproxy.relay

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.api.DefaultApi
import io.mrnateriver.smsproxy.auth.HttpBearerAuth
import io.mrnateriver.smsproxy.infrastructure.ApiClient
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import java.util.logging.Level

@Module
@InstallIn(SingletonComponent::class)
class ProxyApiModule {
    @Provides
    fun providesApiClient(observabilityService: ObservabilityService): ApiClient {
        val proxyApiKey = BuildConfig.API_KEY
        val proxyApiBaseUrl = BuildConfig.API_BASE_URL

        return ApiClient(
            proxyApiBaseUrl,
            OkHttpClient.Builder()
                .readTimeout(BuildConfig.API_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .connectTimeout(BuildConfig.API_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .writeTimeout(BuildConfig.API_TIMEOUT_MS, TimeUnit.MILLISECONDS),
        )
            .addAuthorization("Bearer", HttpBearerAuth(proxyApiKey))
            .apply {
                if (BuildConfig.DEBUG) {
                    logger = { message -> observabilityService.log(Level.FINEST, message) }
                }
            }
    }

    @Provides
    fun providesProxyApiService(client: ApiClient): DefaultApi {
        return client.createService(DefaultApi::class.java)
    }
}