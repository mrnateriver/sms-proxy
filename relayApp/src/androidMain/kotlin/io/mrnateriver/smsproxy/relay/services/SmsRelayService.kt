@file:Suppress("DEPRECATION")

package io.mrnateriver.smsproxy.relay.services

import android.content.Context
import android.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import io.mrnateriver.smsproxy.api.DefaultApi
import io.mrnateriver.smsproxy.models.MessageProxyRequest
import io.mrnateriver.smsproxy.relay.settings.PREF_KEY_API_SERVER_RECEIVER_KEY
import io.mrnateriver.smsproxy.shared.SmsEntry
import javax.inject.Inject
import io.mrnateriver.smsproxy.shared.SmsRelayService as SmsRelayServiceContract

class SmsRelayService @Inject constructor(
    private val proxyApiService: DefaultApi,
    @ApplicationContext private val context: Context,
) :
    SmsRelayServiceContract {
    override suspend fun relay(entry: SmsEntry) {
        val receiverKey = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PREF_KEY_API_SERVER_RECEIVER_KEY, "")
        if (receiverKey.isNullOrBlank()) {
            throw IllegalStateException("Receiver key is not set.")
        }

        proxyApiService.messagesProxy(
            MessageProxyRequest(
                receiverKey,
                entry.smsData.sender,
                entry.smsData.message,
                entry.smsData.receivedAt,
            )
        )
    }
}