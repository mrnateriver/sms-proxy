package io.mrnateriver.smsproxy.server.data.contracts

import io.mrnateriver.smsproxy.shared.models.MessageData

interface MessageSerializer {
    suspend fun serialize(message: MessageData): ByteArray
}
