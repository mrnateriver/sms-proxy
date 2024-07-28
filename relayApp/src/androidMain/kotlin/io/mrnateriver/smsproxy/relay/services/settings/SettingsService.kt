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

private val PREF_KEY_API_SERVER_ADDRESS = stringPreferencesKey("api-server-address")
private val PREF_KEY_API_SERVER_RECEIVER_KEY = stringPreferencesKey("api-server-receiver-key")
private val PREF_KEY_SHOW_RECENT_MESSAGES = booleanPreferencesKey("show-recent-messages")

class SettingsService @Inject constructor(@ApplicationContext private val context: Context) {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val serverAddress: Flow<String>
        get() = context.settingsStore.data.map {
            it[PREF_KEY_API_SERVER_ADDRESS] ?: ""
        }.shareIn(scope, SharingStarted.Lazily, 1)

    val receiverKey: Flow<String>
        get() = context.settingsStore.data.map {
            it[PREF_KEY_API_SERVER_RECEIVER_KEY] ?: ""
        }.shareIn(scope, SharingStarted.Lazily, 1)

    val showRecentMessages: Flow<Boolean>
        get() = context.settingsStore.data.map {
            it[PREF_KEY_SHOW_RECENT_MESSAGES] ?: true
        }.shareIn(scope, SharingStarted.Lazily, 1)

    val isServerConfigured: Flow<Boolean> =
        combine(serverAddress, receiverKey) { serverAddress, receiverKey ->
            serverAddress.isNotEmpty() && receiverKey.isNotEmpty()
        }

    suspend fun setServerAddress(value: String) {
        context.settingsStore.edit { it[PREF_KEY_API_SERVER_ADDRESS] = value }
    }

    suspend fun setReceiverKey(value: String) {
        context.settingsStore.edit { it[PREF_KEY_API_SERVER_RECEIVER_KEY] = value }
    }

    suspend fun setShowRecentMessages(value: Boolean) {
        context.settingsStore.edit { it[PREF_KEY_SHOW_RECENT_MESSAGES] = value }
    }
}
