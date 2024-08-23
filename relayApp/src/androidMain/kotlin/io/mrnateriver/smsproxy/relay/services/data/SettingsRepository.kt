package io.mrnateriver.smsproxy.relay.services.data

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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.SettingsRepository as SettingsRepositoryContract

private val PREF_KEY_API_BASE_API_URL = stringPreferencesKey("api-base-url")
private val PREF_KEY_API_RECEIVER_KEY = stringPreferencesKey("api-receiver-key")
private val PREF_KEY_SHOW_RECENT_MESSAGES = booleanPreferencesKey("show-recent-messages")

class SettingsRepository(
    private val settings: DataStore<Preferences>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
) : SettingsRepositoryContract {
    override val baseApiUrl = getSetting(PREF_KEY_API_BASE_API_URL, "")
    override val receiverKey = getSetting(PREF_KEY_API_RECEIVER_KEY, "")
    override val showRecentMessages = getSetting(PREF_KEY_SHOW_RECENT_MESSAGES, true)

    override suspend fun setBaseApiUrl(value: String) {
        settings.edit { it[PREF_KEY_API_BASE_API_URL] = value }
    }

    override suspend fun setReceiverKey(value: String) {
        settings.edit { it[PREF_KEY_API_RECEIVER_KEY] = value }
    }

    override suspend fun setShowRecentMessages(value: Boolean) {
        settings.edit { it[PREF_KEY_SHOW_RECENT_MESSAGES] = value }
    }

    private fun <T> getSetting(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return settings.data.map {
            it[key] ?: defaultValue
        }.shareIn(scope, SharingStarted.Lazily, 1)
    }
}