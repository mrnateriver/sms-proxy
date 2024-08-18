package io.mrnateriver.smsproxy.relay.services

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.shared.AndroidObservabilityService
import io.mrnateriver.smsproxy.shared.MessageProcessingService
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService as MessageRelayServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

@Module
@InstallIn(SingletonComponent::class)
abstract class ServicesModule {
    @Binds
    abstract fun bindsMessageRelayService(impl: MessageRelayService): MessageRelayServiceContract

    @Binds
    abstract fun bindsMessageStatsService(impl: MessageStatsService): MessageStatsServiceContract

    @Module
    @InstallIn(SingletonComponent::class)
    class MessageProcessingModule {
        @Provides
        fun providesObservabilityService(): ObservabilityServiceContract =
            AndroidObservabilityService()

        @Provides
        fun providesMessageProcessingService(
            repository: MessageRepositoryContract,
            relay: MessageRelayServiceContract,
            observability: ObservabilityServiceContract,
        ): MessageProcessingServiceContract = MessageProcessingService(
            repository = repository,
            relay = relay,
            observability = observability,
        )
    }
}
