package io.mrnateriver.smsproxy.relay.services.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

private val PREF_KEY_API_BASE_API_URL = stringPreferencesKey("api-base-url")
private val PREF_KEY_API_RECEIVER_KEY = stringPreferencesKey("api-receiver-key")
private val PREF_KEY_SHOW_RECENT_MESSAGES = booleanPreferencesKey("show-recent-messages")

interface SettingsServiceContract {
    val baseApiUrl: Flow<String>
    val receiverKey: Flow<String>
    val showRecentMessages: Flow<Boolean>
    val isApiConfigured: Flow<Boolean>

    suspend fun setBaseApiUrl(value: String)
    suspend fun setReceiverKey(value: String)
    suspend fun setShowRecentMessages(value: Boolean)
}

class SettingsService(
    private val settingsStore: DataStore<Preferences>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
) : SettingsServiceContract {
    override val baseApiUrl = getSetting(PREF_KEY_API_BASE_API_URL, "")
    override val receiverKey = getSetting(PREF_KEY_API_RECEIVER_KEY, "")
    override val showRecentMessages = getSetting(PREF_KEY_SHOW_RECENT_MESSAGES, true)

    override val isApiConfigured: Flow<Boolean> =
        combine(baseApiUrl, receiverKey) { baseApiUrl, receiverKey ->
            baseApiUrl.isNotEmpty() && receiverKey.isNotEmpty()
        }

    override suspend fun setBaseApiUrl(value: String) {
        settingsStore.edit { it[PREF_KEY_API_BASE_API_URL] = value }
    }

    override suspend fun setReceiverKey(value: String) {
        settingsStore.edit { it[PREF_KEY_API_RECEIVER_KEY] = value }
    }

    override suspend fun setShowRecentMessages(value: Boolean) {
        settingsStore.edit { it[PREF_KEY_SHOW_RECENT_MESSAGES] = value }
    }

    private fun <T> getSetting(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return settingsStore.data.map {
            it[key] ?: defaultValue
        }.shareIn(scope, SharingStarted.Lazily, 1)
    }
}
