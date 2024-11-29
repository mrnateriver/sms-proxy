package io.mrnateriver.smsproxy.server.data

import arrow.core.right
import io.mrnateriver.smsproxy.server.db.Messages
import io.mrnateriver.smsproxy.server.db.MessagesQueries
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.datetime.toKotlinInstant
import javax.inject.Inject
import io.mrnateriver.smsproxy.server.data.contracts.MessageSerializer as MessageSerializerContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract

class MessageRepository @Inject constructor(
    private val messagesDatabaseQueries: MessagesQueries,
    private val messageSerializer: MessageSerializerContract,
) : MessageRepositoryContract {
    override suspend fun insert(entry: MessageData): MessageEntry {
        val serializedMessage = messageSerializer.serialize(entry)
        return messagesDatabaseQueries.insert(serializedMessage).executeAsOne().toMessageEntry()
    }

    override suspend fun update(entry: MessageEntry): MessageEntry {
        return messagesDatabaseQueries.update(
            guid = entry.guid,
            externalId = entry.externalId,
            sendStatus = entry.sendStatus,
            sendRetries = entry.sendRetries,
            sendFailureReason = entry.sendFailureReason,
        ).executeAsOne().toMessageEntry()
    }

    override suspend fun getAll(vararg statuses: MessageRelayStatus): List<MessageEntry> {
        return messagesDatabaseQueries.getAll(statuses.asList()).executeAsList().map { it.toMessageEntry() }
    }

    override suspend fun getLastEntries(limit: Int): List<MessageEntry> {
        return messagesDatabaseQueries.getLastEntries(limit.toLong()).executeAsList().map { it.toMessageEntry() }
    }

    override suspend fun getLastEntryByStatus(vararg statuses: MessageRelayStatus): MessageEntry? {
        return messagesDatabaseQueries.getLastEntryByStatus(statuses.asList()).executeAsOneOrNull()?.toMessageEntry()
    }

    override suspend fun getCountByStatus(vararg statuses: MessageRelayStatus): Int {
        return messagesDatabaseQueries.getCountByStatus(statuses.asList()).executeAsOne().toInt()
    }

    override suspend fun getCount(): Int {
        return messagesDatabaseQueries.getCount().executeAsOne().toInt()
    }
}

private fun Messages.toMessageEntry(): MessageEntry {
    return MessageEntry(
        guid = guid,
        externalId = externalId,
        sendStatus = sendStatus,
        sendRetries = sendRetries,
        sendFailureReason = sendFailureReason,
        messageData = messageData.right(),
        createdAt = createdAt.toInstant().toKotlinInstant(),
        updatedAt = updatedAt?.toInstant()?.toKotlinInstant(),
    )
}
