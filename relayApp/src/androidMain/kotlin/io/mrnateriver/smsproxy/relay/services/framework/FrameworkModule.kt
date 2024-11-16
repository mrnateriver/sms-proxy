package io.mrnateriver.smsproxy.relay.services.framework

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.shared.services.AndroidObservabilityService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageProcessingScheduler as MessageProcessingSchedulerContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

@Module
@InstallIn(SingletonComponent::class)
class FrameworkModule {
    @Provides
    @Singleton
    fun providesObservabilityService(): ObservabilityServiceContract =
        AndroidObservabilityService()

    @Provides
    @Singleton
    fun providesSmsIntentParserService(): SmsIntentParserService = SmsIntentParserService()

    @Provides
    @Singleton
    fun providesMessageProcessingScheduler(@ApplicationContext context: Context): MessageProcessingSchedulerContract =
        MessageProcessingScheduler(context)

    @Provides
    @Singleton
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
