package io.mrnateriver.smsproxy.relay.services.data

import arrow.core.left
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class MessageRepositoryTest {
    private val messageDao = mock<MessageDao> { }
    private val now = Instant.fromEpochMilliseconds(1723996071981)
    private val mockClock = mock<Clock> { on(it.now()).thenReturn(now) }
    private val subject = MessageRepository(messageDao, mockClock)

    @Test
    fun messageRepository_shouldInsertMessageEntryAndSetTimestampsToCurrentTime() = runTest {
        val entry = MessageData(
            sender = "sender",
            message = "message",
            receivedAt = Instant.fromEpochMilliseconds(12345),
        )

        val result = subject.insert(entry)

        assertEquals(
            MessageEntry(
                guid = result.guid,
                externalId = null,
                sendStatus = MessageRelayStatus.PENDING,
                sendRetries = 0,
                sendFailureReason = null,
                messageData = entry.left(),
                createdAt = now,
                updatedAt = now,
            ),
            result,
        )
    }
}
