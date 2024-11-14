package io.mrnateriver.smsproxy.relay.services.usecases.contracts

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val baseApiUrl: Flow<String>
    val receiverKey: Flow<String>
    val showRecentMessages: Flow<Boolean>

    suspend fun setBaseApiUrl(value: String)
    suspend fun setReceiverKey(value: String)
    suspend fun setShowRecentMessages(value: Boolean)
}
