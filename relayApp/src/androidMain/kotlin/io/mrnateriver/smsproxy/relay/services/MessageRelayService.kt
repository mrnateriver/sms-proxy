@file:Suppress("DEPRECATION")

package io.mrnateriver.smsproxy.relay.services

import android.content.Context
import android.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.mrnateriver.smsproxy.api.DefaultApi
import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.relay.settings.PREF_KEY_API_SERVER_RECEIVER_KEY
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import javax.inject.Inject
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService as MessageRelayServiceContract

class MessageRelayService @Inject constructor(
    private val proxyApiService: DefaultApi,
    @ApplicationContext private val context: Context,
) : MessageRelayServiceContract {
    override suspend fun relay(entry: MessageEntry) {
        val receiverKey = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PREF_KEY_API_SERVER_RECEIVER_KEY, "")
        if (receiverKey.isNullOrBlank()) {
            throw IllegalStateException("Receiver key is not set.")
        }

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