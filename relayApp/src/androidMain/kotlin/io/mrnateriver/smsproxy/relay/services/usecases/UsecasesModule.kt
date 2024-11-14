package io.mrnateriver.smsproxy.relay.services.usecases

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.shared.services.MessageProcessingService
import io.mrnateriver.smsproxy.shared.services.ProxyApiClientFactory
import javax.inject.Singleton
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageBackgroundProcessingService as MessageBackgroundProcessingServiceContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageProcessingScheduler as MessageProcessingSchedulerContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageReceiverService as MessageReceiverServiceContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageStatsRepository as MessageStatsRepositoryContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageStatsService as MessageStatsServiceContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.SettingsRepository as SettingsRepositoryContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.SettingsService as SettingsServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService as MessageRelayServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

@Module
@InstallIn(SingletonComponent::class)
abstract class UsecasesModule {
    @Binds
    @Singleton
    abstract fun bindsMessageProcessingWorkerService(
        impl: MessageBackgroundProcessingService,
    ): MessageBackgroundProcessingServiceContract

    @Module
    @InstallIn(SingletonComponent::class)
    object MessageProcessingModule {
        @Provides
        @Singleton
        fun providesMessageReceiverService(
            smsProcessingService: MessageProcessingServiceContract,
            statsService: MessageStatsServiceContract,
            observabilityService: ObservabilityServiceContract,
            workerScheduler: MessageProcessingSchedulerContract,
        ): MessageReceiverServiceContract {
            return MessageReceiverService(
                smsProcessingService,
                statsService,
                observabilityService,
                workerScheduler,
            )
        }

        @Provides
        @Singleton
        fun providesMessageRelayService(
            apiClientFactory: ProxyApiClientFactory,
            settingsService: SettingsServiceContract,
            observabilityService: ObservabilityServiceContract,
        ): MessageRelayServiceContract {
            return MessageRelayService(apiClientFactory, settingsService, observabilityService)
        }

        @Provides
        @Singleton
        fun providesMessageStatsService(
            observabilityService: ObservabilityServiceContract,
            statsRepository: MessageStatsRepositoryContract,
            messagesRepository: MessageRepositoryContract,
        ): MessageStatsServiceContract {
            return MessageStatsService(observabilityService, statsRepository, messagesRepository)
        }

        @Provides
        @Singleton
        fun providesSettingsService(
            settingsRepository: SettingsRepositoryContract,
        ): SettingsServiceContract {
            return SettingsService(settingsRepository)
        }

        @Provides
        @Singleton
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
