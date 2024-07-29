package io.mrnateriver.smsproxy.relay.services

import io.mrnateriver.smsproxy.api.DefaultApi
import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.relay.services.settings.SettingsService
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.timeout
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService as MessageRelayServiceContract

private const val API_SETTINGS_TIMEOUT_SECONDS = 1

@Singleton
class MessageRelayService @Inject constructor(
    private val apiClientFactory: MessageRelayApiClientFactory,
    private val settingsService: SettingsService,
) : MessageRelayServiceContract {
    private lateinit var apiClient: Flow<Pair<String, DefaultApi>>

    override suspend fun relay(entry: MessageEntry) = coroutineScope {
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
    private suspend fun getApiClient(): Pair<String, DefaultApi> = coroutineScope {
        if (!::apiClient.isInitialized) {
            apiClient = combine(
                settingsService.receiverKey.distinctUntilChanged().onEach {
                    if (it.isBlank()) {
                        throw IllegalStateException("Receiver key is not set")
                    }
                },

                settingsService.baseApiUrl.distinctUntilChanged().map {
                    if (it.isBlank()) {
                        throw IllegalStateException("Base API URL is not set")
                    }
                    apiClientFactory.create(it)
                },

                { key, api -> key to api },
            ).shareIn(CoroutineScope(Dispatchers.IO), SharingStarted.Lazily, 1)
        }

        apiClient.timeout(API_SETTINGS_TIMEOUT_SECONDS.seconds).first()
    }

}