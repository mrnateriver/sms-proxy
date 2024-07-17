package io.mrnateriver.smsproxy.shared

import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.any
import org.mockito.kotlin.atMost
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class MessageProcessingServiceProcessEntryTest : MessageProcessingServiceTestBase() {
    @Test
    fun `when processing entry should save it to the repository`() = runTest {
        val msgData = createTestMessageData()
        val msgEntry = createTestMessageEntry(msgData)

        whenever(mockRepository.insert(any())).thenReturn(msgEntry)

        subject.process(msgData)

        verify(mockRepository, times(1)).insert(msgData)
    }

    @Test
    fun `when processing entry should set its status to in progress`() = runTest {
        val msgData = createTestMessageData()
        val msgEntry = createTestMessageEntry(msgData)

        whenever(mockRepository.insert(any())).thenReturn(msgEntry)

        subject.process(msgData)

        verify(mockRepository).update(
            eq(
                msgEntry.copy(
                    sendStatus = MessageRelayStatus.IN_PROGRESS,
                    sendRetries = 1u,
                ),
            ),
        )
    }

    @Test
    fun `when processing entry should relay it`() = runTest {
        val msgData = createTestMessageData()
        val msgEntry = createTestMessageEntry(msgData)

        whenever(mockRepository.insert(any())).thenReturn(msgEntry)

        subject.process(msgData)

        verify(mockRelayService, atMost(1)).relay(msgEntry)
    }

    @Test
    fun `when processing entry should update its status to success`() = runTest {
        val msgData = createTestMessageData()
        val msgEntry = createTestMessageEntry(msgData)

        whenever(mockRepository.insert(any())).thenReturn(msgEntry)

        val result = subject.process(msgData)

        verify(mockRepository).update(eq(msgEntry.copy(sendStatus = MessageRelayStatus.SUCCESS)))
        assertEquals(MessageRelayStatus.SUCCESS, result.sendStatus)
    }

    @Test
    fun `when processing entry should return entry with updated values`() = runTest {
        val msgData = createTestMessageData()
        val msgEntry = createTestMessageEntry(msgData)

        whenever(mockRepository.insert(any())).thenReturn(msgEntry)

        val result = subject.process(msgData)

        assertEquals(msgEntry.guid, result.guid)
        assertEquals(MessageRelayStatus.SUCCESS, result.sendStatus)
    }

    @Test
    fun `when processing entry and relaying fails should set status to error`() = runTest {
        val msgData = createTestMessageData()
        val msgEntry = createTestMessageEntry(msgData, MessageRelayStatus.PENDING)

        whenever(mockRepository.insert(any())).thenReturn(msgEntry)
        whenever(mockRelayService.relay(any())).thenThrow(RuntimeException("test"))

        subject.process(msgData)

        verify(mockRepository).update(
            eq(
                msgEntry.copy(
                    sendStatus = MessageRelayStatus.ERROR,
                    sendFailureReason = "java.lang.RuntimeException: test"
                ),
            ),
        )
    }

    @Test
    fun `when processing entry and storing it fails should throw exception`() = runTest {
        val msgData = createTestMessageData()

        whenever(mockRepository.insert(any())).thenThrow(RuntimeException("test"))

        assertFails {
            subject.process(msgData)
        }
    }

    @Test
    fun `when processing entry and relaying fails should throw exception`() = runTest {
        val msgData = createTestMessageData()
        val msgEntry = createTestMessageEntry(msgData, MessageRelayStatus.PENDING)

        whenever(mockRepository.insert(any())).thenReturn(msgEntry)
        whenever(mockRelayService.relay(any())).thenThrow(RuntimeException("test"))

        assertFails {
            subject.process(msgData)
        }
    }

}