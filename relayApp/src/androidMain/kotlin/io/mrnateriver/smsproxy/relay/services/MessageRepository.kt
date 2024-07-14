package io.mrnateriver.smsproxy.relay.services

import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as SmsRepositoryContract

class MessageRepository : SmsRepositoryContract {
    override suspend fun insert(entry: MessageData): MessageEntry {
        TODO("Not yet implemented")
    }

    override suspend fun update(entry: MessageEntry): MessageEntry {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(vararg statuses: MessageRelayStatus): List<MessageEntry> {
        TODO("Not yet implemented")
    }

    override suspend fun getCount(): Int {
        TODO("Not yet implemented")
    }
}