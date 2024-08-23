package io.mrnateriver.smsproxy.relay.services.usecases

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.SettingsRepository as SettingsRepositoryContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.SettingsService as SettingsServiceContract

class SettingsService(
    private val settings: SettingsRepositoryContract,
) : SettingsServiceContract {
    override val baseApiUrl = settings.baseApiUrl
    override val receiverKey = settings.receiverKey
    override val showRecentMessages = settings.showRecentMessages

    override val isApiConfigured: Flow<Boolean> =
        combine(baseApiUrl, receiverKey) { baseApiUrl, receiverKey ->
            baseApiUrl.isNotEmpty() && receiverKey.isNotEmpty()
        }

    override suspend fun setBaseApiUrl(value: String) {
        settings.setBaseApiUrl(value)
    }

    override suspend fun setReceiverKey(value: String) {
        settings.setReceiverKey(value)
    }

    override suspend fun setShowRecentMessages(value: Boolean) {
        settings.setShowRecentMessages(value)
    }
}
