package io.mrnateriver.smsproxy.shared

import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.mockito.kotlin.any
import org.mockito.kotlin.anyVararg
import org.mockito.kotlin.argThat
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MessageProcessingServiceHandleUnprocessedTest : MessageProcessingServiceTestBase() {
    @Test
    fun `when handling unprocessed entries should request only error, pending and in progress entries`() =
        runTest {
            whenever(mockRepository.getAll(anyVararg(MessageRelayStatus::class)))
                .thenReturn(emptyList())

            subject.handleUnprocessedMessages()

            verify(mockRepository).getAll(
                MessageRelayStatus.ERROR,
                MessageRelayStatus.PENDING,
                MessageRelayStatus.IN_PROGRESS,
            )
        }

    @Test
    fun `when handling unprocessed entries should process every returned entry`() = runTest {
        val msgData = createTestMessageData()
        val msgEntries = listOf(
            createTestMessageEntry(msgData, MessageRelayStatus.ERROR),
            createTestMessageEntry(msgData, MessageRelayStatus.ERROR),
            createTestMessageEntry(msgData, MessageRelayStatus.PENDING),
            createTestMessageEntry(msgData, MessageRelayStatus.PENDING),
        )

        whenever(mockRepository.getAll(anyVararg(MessageRelayStatus::class)))
            .thenReturn(msgEntries)

        subject.handleUnprocessedMessages()

        verify(mockRelayService, times(msgEntries.size)).relay(any<MessageEntry>())
    }

    @Test
    fun `when handling unprocessed entries should not relay already processed entries`() = runTest {
        val msgData = createTestMessageData()
        val msgEntries = listOf(
            createTestMessageEntry(msgData, MessageRelayStatus.SUCCESS),
            createTestMessageEntry(msgData, MessageRelayStatus.SUCCESS),
        )

        whenever(mockRepository.getAll(anyVararg(MessageRelayStatus::class)))
            .thenReturn(msgEntries)

        subject.handleUnprocessedMessages()

        verify(mockRelayService, never()).relay(any<MessageEntry>())
    }

    @Test
    fun `when handling unprocessed entries should not relay failed entries`() = runTest {
        val msgData = createTestMessageData()
        val msgEntries = listOf(
            createTestMessageEntry(msgData, MessageRelayStatus.FAILED),
            createTestMessageEntry(msgData, MessageRelayStatus.FAILED),
        )

        whenever(mockRepository.getAll(anyVararg(MessageRelayStatus::class)))
            .thenReturn(msgEntries)

        subject.handleUnprocessedMessages()

        verify(mockRelayService, never()).relay(any<MessageEntry>())
    }

    @Test
    fun `when handling unprocessed entries should not relay entries which are already in progress`() =
        runTest {
            val msgData = createTestMessageData()
            val msgEntries = listOf(
                createTestMessageEntry(msgData, MessageRelayStatus.IN_PROGRESS),
                createTestMessageEntry(msgData, MessageRelayStatus.IN_PROGRESS),
            )

            whenever(mockRepository.getAll(anyVararg(MessageRelayStatus::class)))
                .thenReturn(msgEntries)

            subject.handleUnprocessedMessages()

            verify(mockRelayService, never()).relay(any<MessageEntry>())
        }

    @Test
    fun `when handling unprocessed entries should mark them as failed if they have been retried too many times`() =
        runTest {
            val msgData = createTestMessageData()
            val msgEntry = createTestMessageEntry(
                msgData,
                MessageRelayStatus.PENDING,
                mockProcessingConfig.maxRetries.dec(),
            )

            whenever(mockRepository.getAll(anyVararg(MessageRelayStatus::class)))
                .thenReturn(listOf(msgEntry))
            whenever(mockRelayService.relay(any())).thenThrow(RuntimeException("test"))

            subject.handleUnprocessedMessages()

            verify(mockRepository).update(argThat { sendStatus == MessageRelayStatus.FAILED })
        }

    @Test
    fun `when handling unprocessed entries that's been stuck in progress should abort the relaying and record error`() =
        runTest {
            val msgData = createTestMessageData()
            val msgEntry = createTestMessageEntry(
                msgData,
                MessageRelayStatus.IN_PROGRESS,
                1,
                Instant.fromEpochMilliseconds(0),
                Instant.fromEpochSeconds(10),
            )

            whenever(
                mockClock.now(),
            ).thenReturn(
                Instant.fromEpochMilliseconds(
                    (10.seconds + mockProcessingConfig.timeout + 1.milliseconds).inWholeMilliseconds,
                ),
            )
            whenever(mockRepository.getAll(anyVararg(MessageRelayStatus::class)))
                .thenReturn(listOf(msgEntry))

            subject.handleUnprocessedMessages()

            verify(mockRepository).update(
                argThat {
                    sendStatus == MessageRelayStatus.ERROR &&
                        sendFailureReason != null &&
                        sendFailureReason!!.contains("stuck in progress")
                },
            )
        }

    @Test
    fun `when handling unprocessed entries should process entries with final status`() =
        runTest {
            val msgData = createTestMessageData()
            val msgEntries = listOf(
                createTestMessageEntry(msgData, MessageRelayStatus.ERROR),
                createTestMessageEntry(msgData, MessageRelayStatus.PENDING),
                createTestMessageEntry(msgData, MessageRelayStatus.IN_PROGRESS),
            )

            whenever(mockRepository.getAll(anyVararg(MessageRelayStatus::class)))
                .thenReturn(msgEntries)

            val results = subject.handleUnprocessedMessages().toList()

            assertEquals(msgEntries.size, results.size)
            assertEquals(
                listOf(
                    MessageRelayStatus.SUCCESS,
                    MessageRelayStatus.SUCCESS,
                    MessageRelayStatus.IN_PROGRESS,
                ),
                results.map { it.sendStatus },
            )
        }

    @Test
    fun `when handling unprocessed entries should not throw exception if relaying fails`() =
        runTest {
            val msgData = createTestMessageData()
            val msgEntries = listOf(
                createTestMessageEntry(msgData, MessageRelayStatus.PENDING),
                createTestMessageEntry(msgData, MessageRelayStatus.PENDING),
            )

            whenever(mockRepository.getAll(anyVararg(MessageRelayStatus::class)))
                .thenReturn(msgEntries)
            whenever(mockRelayService.relay(any())).thenThrow(RuntimeException("test"))

            val results = subject.handleUnprocessedMessages().toList()

            assertEquals(msgEntries.size, results.size)
            assertEquals(
                listOf(MessageRelayStatus.ERROR, MessageRelayStatus.ERROR),
                results.map { it.sendStatus },
            )
        }
}
