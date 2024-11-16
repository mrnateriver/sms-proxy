package io.mrnateriver.smsproxy.shared.contracts

import io.mrnateriver.smsproxy.shared.models.MessageEntry

interface MessageRelayService {
    suspend fun relay(entry: MessageEntry)
}
