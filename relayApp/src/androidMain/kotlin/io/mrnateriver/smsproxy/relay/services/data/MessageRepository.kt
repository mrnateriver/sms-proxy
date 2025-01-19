package io.mrnateriver.smsproxy.relay.services.data

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import arrow.core.left
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageWatchService as MessageWatchServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract

class MessageRepository @Inject constructor(
    private val messageDao: MessageDao,
    private val clock: Clock = Clock.System,
) : MessageRepositoryContract, MessageWatchServiceContract {
    override suspend fun insert(entry: MessageData): MessageEntry {
        val now = clock.now()
        val result = MessageEntry(
            guid = UUID.randomUUID(),
            externalId = null,
            sendStatus = MessageRelayStatus.PENDING,
            sendRetries = 0,
            sendFailureReason = null,
            messageData = entry.left(),
            createdAt = now,
            updatedAt = now,
        )
        return result.also { messageDao.insert(it.toDatabaseEntity()) }
    }

    override suspend fun update(entry: MessageEntry): MessageEntry {
        return entry.also {
            messageDao.update(
                it.toDatabaseEntity()
                    .copy(updatedAt = Clock.System.now()),
            )
        }
    }

    override suspend fun getAll(vararg statuses: MessageRelayStatus): List<MessageEntry> {
        return messageDao.getAll(*statuses).map { it.toDomainEntity() }
    }

    override suspend fun getLastEntries(limit: Int): List<MessageEntry> {
        return messageDao.getLastEntries(limit).map { it.toDomainEntity() }
    }

    override suspend fun getLastEntryByStatus(vararg statuses: MessageRelayStatus): MessageEntry? {
        return messageDao.getLastEntryByStatus(*statuses)?.toDomainEntity()
    }

    override suspend fun getCountByStatus(vararg statuses: MessageRelayStatus): Int {
        return messageDao.getCountByStatuses(*statuses)
    }

    override suspend fun getCount(): Int {
        return messageDao.getCount()
    }

    override fun watchLastEntries(limit: Int): Flow<List<MessageEntry>> {
        return messageDao.observeLastEntries(limit).map { it.map { it.toDomainEntity() } }.asFlow()
    }
}

private fun MessageDaoEntity.toDomainEntity() = MessageEntry(
    guid = guid,
    externalId = externalId,
    sendStatus = sendStatus,
    sendRetries = sendRetries,
    sendFailureReason = sendFailureReason,
    messageData = messageData.left(),
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun MessageEntry.toDatabaseEntity() = MessageDaoEntity(
    guid = guid,
    externalId = externalId,
    sendStatus = sendStatus,
    sendRetries = sendRetries,
    sendFailureReason = sendFailureReason,
    messageData = messageData.leftOrNull()!!,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
