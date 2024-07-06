package io.mrnateriver.smsproxy.shared

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
import kotlin.time.Duration.Companion.seconds

class SmsProcessingServiceHandleUnprocessedTest : SmsProcessingServiceTestBase() {
    @Test
    fun `when handling unprocessed entries should request only error, pending and in progress entries`() =
        runTest {
            whenever(mockRepository.getAll(anyVararg(SmsRelayStatus::class)))
                .thenReturn(emptyList())

            subject.handleUnprocessedMessages()

            verify(mockRepository).getAll(
                SmsRelayStatus.ERROR,
                SmsRelayStatus.PENDING,
                SmsRelayStatus.IN_PROGRESS
            )
        }

    @Test
    fun `when handling unprocessed entries should process every returned entry`() = runTest {
        val smsData = createTestSmsData()
        val smsEntries = listOf(
            createTestSmsEntry(smsData, SmsRelayStatus.ERROR),
            createTestSmsEntry(smsData, SmsRelayStatus.ERROR),
            createTestSmsEntry(smsData, SmsRelayStatus.PENDING),
            createTestSmsEntry(smsData, SmsRelayStatus.PENDING),
        )

        whenever(mockRepository.getAll(anyVararg(SmsRelayStatus::class)))
            .thenReturn(smsEntries)

        subject.handleUnprocessedMessages()

        verify(mockRelayService, times(smsEntries.size)).relay(any<SmsEntry>())
    }

    @Test
    fun `when handling unprocessed entries should not relay already processed entries`() = runTest {
        val smsData = createTestSmsData()
        val smsEntries = listOf(
            createTestSmsEntry(smsData, SmsRelayStatus.SUCCESS),
            createTestSmsEntry(smsData, SmsRelayStatus.SUCCESS),
        )

        whenever(mockRepository.getAll(anyVararg(SmsRelayStatus::class)))
            .thenReturn(smsEntries)

        subject.handleUnprocessedMessages()

        verify(mockRelayService, never()).relay(any<SmsEntry>())
    }

    @Test
    fun `when handling unprocessed entries should not relay failed entries`() = runTest {
        val smsData = createTestSmsData()
        val smsEntries = listOf(
            createTestSmsEntry(smsData, SmsRelayStatus.FAILED),
            createTestSmsEntry(smsData, SmsRelayStatus.FAILED),
        )

        whenever(mockRepository.getAll(anyVararg(SmsRelayStatus::class)))
            .thenReturn(smsEntries)

        subject.handleUnprocessedMessages()

        verify(mockRelayService, never()).relay(any<SmsEntry>())
    }

    @Test
    fun `when handling unprocessed entries should not relay entries which are already in progress`() =
        runTest {
            val smsData = createTestSmsData()
            val smsEntries = listOf(
                createTestSmsEntry(smsData, SmsRelayStatus.IN_PROGRESS),
                createTestSmsEntry(smsData, SmsRelayStatus.IN_PROGRESS),
            )

            whenever(mockRepository.getAll(anyVararg(SmsRelayStatus::class)))
                .thenReturn(smsEntries)

            subject.handleUnprocessedMessages()

            verify(mockRelayService, never()).relay(any<SmsEntry>())
        }

    @Test
    fun `when handling unprocessed entries should mark them as failed if they have been retried too many times`() =
        runTest {
            val smsData = createTestSmsData()
            val smsEntry = createTestSmsEntry(
                smsData,
                SmsRelayStatus.PENDING,
                mockProcessingConfig.maxRetries.inc()
            )

            whenever(mockRepository.getAll(anyVararg(SmsRelayStatus::class)))
                .thenReturn(listOf(smsEntry))

            subject.handleUnprocessedMessages()

            verify(mockRelayService, never()).relay(smsEntry)
            verify(mockRepository).update(argThat { sendStatus == SmsRelayStatus.FAILED })
        }

    @Test
    fun `when handling unprocessed entries that's been stuck in progress should abort the relaying and record error`() =
        runTest {
            val smsData = createTestSmsData()
            val smsEntry = createTestSmsEntry(
                smsData,
                SmsRelayStatus.IN_PROGRESS,
                1u,
                Instant.fromEpochMilliseconds(0),
                Instant.fromEpochSeconds(10),
            )

            whenever(mockClock.now()).thenReturn(Instant.fromEpochSeconds((10.seconds + mockProcessingConfig.timeout).inWholeSeconds))
            whenever(mockRepository.getAll(anyVararg(SmsRelayStatus::class)))
                .thenReturn(listOf(smsEntry))

            subject.handleUnprocessedMessages()

            verify(mockRepository).update(argThat {
                sendStatus == SmsRelayStatus.ERROR &&
                        sendFailureReason != null &&
                        sendFailureReason!!.contains("stuck in progress")
            })
        }
}