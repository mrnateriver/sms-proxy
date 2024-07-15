package io.mrnateriver.smsproxy.relay

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.api.DefaultApi
import io.mrnateriver.smsproxy.auth.HttpBearerAuth
import io.mrnateriver.smsproxy.infrastructure.ApiClient

@Module
@InstallIn(SingletonComponent::class)
class ProxyApiModule {
    @Provides
    fun providesApiClient(): ApiClient {
        val proxyApiKey = BuildConfig.API_KEY
        val proxyApiBaseUrl = BuildConfig.API_BASE_URL
        return ApiClient(proxyApiBaseUrl).addAuthorization("Bearer", HttpBearerAuth(proxyApiKey))
    }

    @Provides
    fun providesProxyApiService(client: ApiClient): DefaultApi {
        return client.createService(DefaultApi::class.java)
    }
}