package io.mrnateriver.smsproxy.relay.services.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    @Provides
    fun providesSettingsService(@ApplicationContext context: Context): SettingsServiceContract {
        return SettingsService(context.settingsStore)
    }
}
