package io.mrnateriver.smsproxy.relay.services

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.shared.ProxyApiClientFactory
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import io.mrnateriver.smsproxy.shared.createProxyApiClient

@Module
@InstallIn(SingletonComponent::class)
class ProxyApiModule {
    @Provides
    fun providesProxyApiClientFactory(observabilityService: ObservabilityService): ProxyApiClientFactory {
        return ProxyApiClientFactory { createProxyApiClient(observabilityService, it) }
    }
}