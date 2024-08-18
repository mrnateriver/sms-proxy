package io.mrnateriver.smsproxy.relay.services.settings

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    abstract fun bindSettingsService(impl: SettingsService): SettingsServiceContract

}