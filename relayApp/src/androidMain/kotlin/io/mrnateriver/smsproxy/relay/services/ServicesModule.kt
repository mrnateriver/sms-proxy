package io.mrnateriver.smsproxy.relay.services

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.shared.AndroidObservabilityService
import io.mrnateriver.smsproxy.shared.MessageProcessingService
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService as MessageRelayServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

@Module
@InstallIn(SingletonComponent::class)
abstract class ServicesModule {
    @Binds
    abstract fun bindsMessageRepository(impl: MessageRepository): MessageRepositoryContract

    @Binds
    abstract fun bindsMessageRelayService(impl: MessageRelayService): MessageRelayServiceContract

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
        ): MessageProcessingService = MessageProcessingService(
            repository = repository,
            relay = relay,
            observability = observability,
        )
    }
}
