package io.mrnateriver.smsproxy.relay.services

import io.mrnateriver.smsproxy.api.DefaultApi
import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.relay.services.settings.SettingsServiceContract
import io.mrnateriver.smsproxy.shared.ProxyApi
import io.mrnateriver.smsproxy.shared.ProxyApiClientFactory
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.timeout
import java.util.logging.Level
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService as MessageRelayServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

private const val API_SETTINGS_TIMEOUT_SECONDS = 1

@Singleton
class MessageRelayService @Inject constructor(
    private val apiClientFactory: ProxyApiClientFactory,
    private val settingsService: SettingsServiceContract,
    private val observabilityService: ObservabilityServiceContract,
) : MessageRelayServiceContract {
    private lateinit var apiClient: Flow<Pair<String, ProxyApi>>

    @kotlin.time.ExperimentalTime
    override suspend fun relay(entry: MessageEntry) {
        val (receiverKey, proxyApiService) = getApiClient()

        val response = proxyApiService.messagesProxy(
            MessageProxyRequest(
                receiverKey,
                entry.messageData.sender,
                entry.messageData.message,
                entry.messageData.receivedAt,
            )
        )

        if (!response.isSuccessful) {
            val body = response.errorBody()?.string()
            throw Exception("Failed to relay message, status code: ${response.code()} ${response.message()}${if (body.isNullOrBlank()) "" else "\n$body"}")
        }
    }

    @OptIn(FlowPreview::class)
    @kotlin.time.ExperimentalTime
    private suspend fun getApiClient(): Pair<String, DefaultApi> {
        if (!::apiClient.isInitialized) {
            apiClient = combine(
                settingsService.receiverKey.distinctUntilChanged(),
                settingsService.baseApiUrl.distinctUntilChanged().map {
                    if (it.isBlank()) {
                        return@map null
                    }

                    apiClientFactory.create(it)
                },

                { key, api -> key to api },
            )
                .catch {
                    // This `catch` is necessary to prevent app crashing on errors, since the subscription is run in a separate global coroutine
                    observabilityService.log(
                        Level.SEVERE,
                        "Unexpected error occurred when reading receiver key or base API URL settings: $it"
                    )
                    observabilityService.reportException(it)
                }
                .shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.Lazily, 1)
                .map { (key, api) ->
                    // Throwing errors on the upstream flow would not result in relay error, as
                    // we'd want it - the upstream coroutine would just be aborted, and the
                    // downstream flow will timeout on any subsequent operation
                    if (key.isBlank()) throw IllegalStateException("Receiver key is not set")
                    if (api == null) throw IllegalStateException("Base API URL is not set")
                    key to api
                }
        }

        return apiClient.timeout(API_SETTINGS_TIMEOUT_SECONDS.seconds).first()
    }

}