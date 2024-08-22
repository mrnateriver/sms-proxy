package io.mrnateriver.smsproxy.relay.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.relay.services.settings.SettingsServiceContract
import io.mrnateriver.smsproxy.shared.services.AndroidObservabilityService
import io.mrnateriver.smsproxy.shared.services.MessageProcessingService
import io.mrnateriver.smsproxy.shared.services.ProxyApiClientFactory
import javax.inject.Singleton
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService as MessageRelayServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "messages_stats")

@Module
@InstallIn(SingletonComponent::class)
abstract class ServicesModule {
    @Binds
    @Singleton
    abstract fun bindsMessageProcessingWorkerService(impl: MessageProcessingWorkerService): MessageProcessingWorkerServiceContract

    @Module
    @InstallIn(SingletonComponent::class)
    object MessageProcessingModule {
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
        fun providesSmsIntentParserService(): SmsIntentParserServiceContract =
            SmsIntentParserService()

        @Provides
        @Singleton
        fun providesObservabilityService(): ObservabilityServiceContract =
            AndroidObservabilityService()

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

        @Provides
        @Singleton
        fun providesMessageStatsService(
            @ApplicationContext context: Context,
            observabilityService: ObservabilityServiceContract,
            messagesRepository: MessageRepositoryContract,
        ): MessageStatsServiceContract {
            return MessageStatsService(context.dataStore, observabilityService, messagesRepository)
        }
    }
}
