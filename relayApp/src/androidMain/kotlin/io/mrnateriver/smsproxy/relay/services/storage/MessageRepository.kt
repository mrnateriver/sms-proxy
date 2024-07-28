package io.mrnateriver.smsproxy.relay.services.storage

import io.mrnateriver.smsproxy.shared.contracts.MessageRepository
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val messagesDao: MessagesDao,
) : MessageRepository {
    override suspend fun insert(entry: MessageData): MessageEntry {
        val now = Clock.System.now()
        val result = MessageEntry(
            guid = UUID.randomUUID(),
            externalId = null,
            sendStatus = MessageRelayStatus.PENDING,
            sendRetries = 0,
            sendFailureReason = null,
            messageData = entry,
            createdAt = now,
            updatedAt = now,
        )
        return result.also { messagesDao.insert(it.toEntity()) }
    }

    override suspend fun update(entry: MessageEntry): MessageEntry {
        return entry.also { messagesDao.update(it.toEntity()) }
    }

    override suspend fun getAll(vararg statuses: MessageRelayStatus): List<MessageEntry> {
        return messagesDao.getAll(*statuses).map { it.toEntry() }
    }

    override suspend fun getLastEntries(limit: Int): List<MessageEntry> {
        return messagesDao.getLastEntries(limit).map { it.toEntry() }
    }

    override suspend fun getLastEntryByStatus(vararg statuses: MessageRelayStatus): MessageEntry? {
        return messagesDao.getLastEntryByStatus(*statuses)?.toEntry()
    }

    override suspend fun getCountByStatus(vararg statuses: MessageRelayStatus): Int {
        return messagesDao.getCountByStatuses(*statuses)
    }

    override suspend fun getCount(): Int {
        return messagesDao.getCount()
    }
}

private fun MessageEntity.toEntry() = MessageEntry(
    guid = guid,
    externalId = externalId,
    sendStatus = sendStatus,
    sendRetries = sendRetries,
    sendFailureReason = sendFailureReason,
    messageData = messageData,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun MessageEntry.toEntity() = MessageEntity(
    guid = guid,
    externalId = externalId,
    sendStatus = sendStatus,
    sendRetries = sendRetries,
    sendFailureReason = sendFailureReason,
    messageData = messageData,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
