package io.mrnateriver.smsproxy.relay.services

import io.mrnateriver.smsproxy.relay.services.data.MessagesDao
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject
import io.mrnateriver.smsproxy.relay.services.data.MessageEntity as MessageDaoEntity
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract

class MessageRepository @Inject constructor(
    private val messagesDao: MessagesDao,
) : MessageRepositoryContract {
    override suspend fun insert(entry: MessageData): MessageEntry {
        val result = MessageEntry(
            guid = UUID.randomUUID(),
            externalId = null,
            sendStatus = MessageRelayStatus.PENDING,
            sendRetries = 0,
            sendFailureReason = null,
            messageData = entry,
            createdAt = Clock.System.now(),
            updatedAt = null,
        )
        return result.also { messagesDao.insert(it.toEntity()) }
    }

    override suspend fun update(entry: MessageEntry): MessageEntry {
        return entry.also { messagesDao.update(it.toEntity()) }
    }

    override suspend fun getAll(vararg statuses: MessageRelayStatus): List<MessageEntry> {
        return messagesDao.getAll(*statuses).map { it.toEntry() }
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

private fun MessageDaoEntity.toEntry() = MessageEntry(
    guid = guid,
    externalId = externalId,
    sendStatus = sendStatus,
    sendRetries = sendRetries,
    sendFailureReason = sendFailureReason,
    messageData = messageData,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun MessageEntry.toEntity() = MessageDaoEntity(
    guid = guid,
    externalId = externalId,
    sendStatus = sendStatus,
    sendRetries = sendRetries,
    sendFailureReason = sendFailureReason,
    messageData = messageData,
    createdAt = createdAt,
    updatedAt = updatedAt,
)