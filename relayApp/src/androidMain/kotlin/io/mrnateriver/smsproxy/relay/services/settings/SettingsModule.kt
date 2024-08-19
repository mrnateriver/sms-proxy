package io.mrnateriver.smsproxy.relay.services.settings

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {
    @Provides
    fun providesSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        return PreferenceDataStoreFactory.create(migrations = listOf(), scope = scope) {
            context.preferencesDataStoreFile("app_settings")
        }
    }

    @Provides
    fun providesSettingsService(settingsDataStore: SettingsDataStore): SettingsServiceContract {
        // Scope is provided by the SettingsService itself by default, but Dagger cannot inject
        // the rest of the constructor arguments, hence this provision function
        return SettingsService(settingsDataStore)
    }
}
