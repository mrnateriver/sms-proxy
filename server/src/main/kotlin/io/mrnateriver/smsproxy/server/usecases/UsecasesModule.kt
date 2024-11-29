package io.mrnateriver.smsproxy.server.usecases

import dagger.Module
import dagger.Provides
import io.mrnateriver.smsproxy.server.data.DataModule
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import io.mrnateriver.smsproxy.shared.services.MessageProcessingService
import javax.inject.Singleton
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract

@Module(includes = [DataModule::class])
interface UsecasesModule {
    companion object {
        @Provides
        @Singleton
        fun provideMessageProcessingService(
            repository: MessageRepository,
            relay: MessageRelayService,
            observability: ObservabilityService,
        ): MessageProcessingServiceContract {
            return MessageProcessingService(repository, relay, observability)
        }
    }
}
