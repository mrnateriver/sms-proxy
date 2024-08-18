package io.mrnateriver.smsproxy.shared.contracts

import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry

interface MessageProcessingService {
    suspend fun process(msg: MessageData): MessageEntry
    suspend fun handleUnprocessedMessages(): Iterable<MessageEntry>
}