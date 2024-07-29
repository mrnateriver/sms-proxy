package io.mrnateriver.smsproxy.relay.services.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

private val PREF_KEY_API_BASE_API_URL = stringPreferencesKey("api-base-url")
private val PREF_KEY_API_RECEIVER_KEY = stringPreferencesKey("api-receiver-key")
private val PREF_KEY_SHOW_RECENT_MESSAGES = booleanPreferencesKey("show-recent-messages")

class SettingsService @Inject constructor(@ApplicationContext private val context: Context) {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val baseApiUrl = getSetting(PREF_KEY_API_BASE_API_URL, "")
    val receiverKey = getSetting(PREF_KEY_API_RECEIVER_KEY, "")
    val showRecentMessages = getSetting(PREF_KEY_SHOW_RECENT_MESSAGES, true)

    val isApiConfigured: Flow<Boolean> =
        combine(baseApiUrl, receiverKey) { baseApiUrl, receiverKey ->
            baseApiUrl.isNotEmpty() && receiverKey.isNotEmpty()
        }

    suspend fun setBaseApiUrl(value: String) {
        context.settingsStore.edit { it[PREF_KEY_API_BASE_API_URL] = value }
    }

    suspend fun setReceiverKey(value: String) {
        context.settingsStore.edit { it[PREF_KEY_API_RECEIVER_KEY] = value }
    }

    suspend fun setShowRecentMessages(value: Boolean) {
        context.settingsStore.edit { it[PREF_KEY_SHOW_RECENT_MESSAGES] = value }
    }

    private fun <T> getSetting(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return context.settingsStore.data.map {
            it[key] ?: defaultValue
        }.shareIn(scope, SharingStarted.Lazily, 1)
    }
}
