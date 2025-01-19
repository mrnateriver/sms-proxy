package io.mrnateriver.smsproxy.relay

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.mrnateriver.smsproxy.relay.services.data.ProxyApiCertificates
import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsData
import io.mrnateriver.smsproxy.shared.API_KEY
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageStatsService as MessageStatsServiceContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageWatchService as MessageWatchServiceContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.SettingsService as SettingsServiceContract

private const val MESSAGE_RECORDS_RECENT_COUNT = 5

@HiltViewModel
class AppViewModel @Inject constructor(
    val settingsService: SettingsServiceContract,
    statsService: MessageStatsServiceContract,
    messageWatchService: MessageWatchServiceContract,
    apiCertificates: ProxyApiCertificates,
) : ViewModel() {
    // Used in tests
    internal var apiKey: String = API_KEY

    val showApiKeyError get() = apiKey.isBlank()

    val showMissingCertificatesError = apiCertificates.serverCertificatePem == null ||
        apiCertificates.clientCertificatePem == null ||
        apiCertificates.clientPrivateKeyPem == null

    val showServerSettingsHint = settingsService.isApiConfigured.map { !it }

    val messageStats: Flow<MessageStatsData> = statsService.getStats()

    @OptIn(ExperimentalCoroutinesApi::class)
    val messageRecordsRecent: Flow<List<MessageEntry>> =
        settingsService.showRecentMessages.flatMapLatest { showRecentMessages ->
            if (showRecentMessages) {
                messageWatchService.watchLastEntries(MESSAGE_RECORDS_RECENT_COUNT)
            } else {
                flowOf(emptyList())
            }
        }
}
