@file:Suppress("DEPRECATION")

package io.mrnateriver.smsproxy.relay.services

import io.mrnateriver.smsproxy.api.DefaultApi
import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.relay.services.settings.SettingsService
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.timeout
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService as MessageRelayServiceContract

private const val RECEIVER_KEY_TIMEOUT_SECONDS = 1

class MessageRelayService @Inject constructor(
    private val proxyApiService: DefaultApi,
    private val settingsService: SettingsService,
) : MessageRelayServiceContract {

    @OptIn(FlowPreview::class)
    override suspend fun relay(entry: MessageEntry) {
        val receiverKey =
            settingsService.receiverKey.timeout(RECEIVER_KEY_TIMEOUT_SECONDS.seconds).first()

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
            throw IllegalStateException(
                "Failed to relay message, status code: ${response.code()} ${response.message()}${if (body.isNullOrBlank()) "" else "\n$body"}"
            )
        }
    }

}