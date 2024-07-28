package io.mrnateriver.smsproxy.shared.contracts

import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus

interface MessageRepository {
    suspend fun insert(entry: MessageData): MessageEntry
    suspend fun update(entry: MessageEntry): MessageEntry
    suspend fun getAll(vararg statuses: MessageRelayStatus): List<MessageEntry>
    suspend fun getLastEntryByStatus(vararg statuses: MessageRelayStatus): MessageEntry?
    suspend fun getCountByStatus(vararg statuses: MessageRelayStatus): Int
    suspend fun getCount(): Int
}