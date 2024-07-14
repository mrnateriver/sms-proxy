package io.mrnateriver.smsproxy.relay

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.api.DefaultApi
import io.mrnateriver.smsproxy.auth.HttpBearerAuth
import io.mrnateriver.smsproxy.infrastructure.ApiClient
import io.mrnateriver.smsproxy.relay.services.MessageRelayService
import io.mrnateriver.smsproxy.relay.services.MessageRepository
import io.mrnateriver.smsproxy.shared.AndroidObservabilityService
import io.mrnateriver.smsproxy.shared.MessageProcessingService
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService as MessageRelayServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
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

    @Provides
    fun providesObservabilityService(): ObservabilityService = AndroidObservabilityService()

    @Provides
    fun providesMessageRepository(impl: MessageRepository): MessageRepositoryContract = impl

    @Provides
    fun providesMessageRelayService(impl: MessageRelayService): MessageRelayServiceContract = impl

    @Provides
    fun providesMessageProcessingService(
        repository: MessageRepositoryContract,
        relay: MessageRelayServiceContract,
        observability: ObservabilityService,
    ): MessageProcessingService {
        return MessageProcessingService(
            repository = repository,
            relay = relay,
            observability = observability,
        )
    }
}