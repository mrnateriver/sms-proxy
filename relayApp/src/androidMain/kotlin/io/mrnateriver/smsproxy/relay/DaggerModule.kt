package io.mrnateriver.smsproxy.relay

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.proxy.api.DefaultApi
import io.mrnateriver.smsproxy.proxy.auth.HttpBearerAuth
import io.mrnateriver.smsproxy.proxy.infrastructure.ApiClient

@Module
@InstallIn(SingletonComponent::class)
class DaggerModule {

    @Provides
    fun providesApiClient(): ApiClient {
        val proxyApiKey = BuildConfig.API_KEY
        val proxyApiBaseUrl = BuildConfig.API_BASE_URL
        return ApiClient(proxyApiBaseUrl).addAuthorization("Bearer", HttpBearerAuth(proxyApiKey))
    }

    @Provides
    fun providesProxyApiService(): DefaultApi {
        return providesApiClient().createService(DefaultApi::class.java)
    }

}