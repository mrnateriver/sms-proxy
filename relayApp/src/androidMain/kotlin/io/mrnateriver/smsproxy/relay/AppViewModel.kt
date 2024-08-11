package io.mrnateriver.smsproxy.relay

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mrnateriver.smsproxy.relay.services.MessageStatsData
import io.mrnateriver.smsproxy.relay.services.MessageStatsService
import io.mrnateriver.smsproxy.relay.services.ProxyApiCertificates
import io.mrnateriver.smsproxy.relay.services.settings.SettingsService
import io.mrnateriver.smsproxy.shared.API_KEY
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract

private const val MESSAGE_RECORDS_RECENT_COUNT = 5

@HiltViewModel
class AppViewModel @Inject constructor(
    val settingsService: SettingsService,
    statsService: MessageStatsService,
    messagesRepository: MessageRepositoryContract,
    apiCertificates: ProxyApiCertificates,
) : ViewModel() {
    val showApiKeyError = API_KEY.isBlank()

    val showMissingCertificatesError = apiCertificates.serverCertificatePem == null ||
            apiCertificates.clientCertificatePem == null ||
            apiCertificates.clientPrivateKeyPem == null

    val showServerSettingsHint = settingsService.isApiConfigured.map { !it }

    val messageStats: Flow<MessageStatsData> = statsService.getStats()

    @OptIn(ExperimentalCoroutinesApi::class)
    val messageRecordsRecent: Flow<List<MessageEntry>> =
        settingsService.showRecentMessages.flatMapLatest { showRecentMessages ->
            if (showRecentMessages) {
                statsService.statsUpdates.map {
                    messagesRepository.getLastEntries(MESSAGE_RECORDS_RECENT_COUNT)
                }
            } else {
                flowOf(emptyList())
            }
        }
}