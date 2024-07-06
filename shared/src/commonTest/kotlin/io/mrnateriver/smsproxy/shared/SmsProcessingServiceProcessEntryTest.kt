package io.mrnateriver.smsproxy.shared

import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.any
import org.mockito.kotlin.atMost
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class SmsProcessingServiceProcessEntryTest : SmsProcessingServiceTestBase() {
    @Test
    fun `when processing entry should save it to the repository`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)

        subject.process(smsData)

        verify(mockRepository, times(1)).insert(smsData)
    }

    @Test
    fun `when processing entry should set its status to in progress`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)

        subject.process(smsData)

        verify(mockRepository).update(
            eq(
                smsEntry.copy(
                    sendStatus = SmsRelayStatus.IN_PROGRESS,
                    sendRetries = 1u,
                ),
            ),
        )
    }

    @Test
    fun `when processing entry should relay it`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)

        subject.process(smsData)

        verify(mockRelayService, atMost(1)).relay(smsEntry)
    }

    @Test
    fun `when processing entry should update its status to success`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)

        val result = subject.process(smsData)

        verify(mockRepository).update(eq(smsEntry.copy(sendStatus = SmsRelayStatus.SUCCESS)))
        assertEquals(SmsRelayStatus.SUCCESS, result.sendStatus)
    }

    @Test
    fun `when processing entry should return entry with updated values`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)

        val result = subject.process(smsData)

        assertEquals(smsEntry.guid, result.guid)
        assertEquals(SmsRelayStatus.SUCCESS, result.sendStatus)
    }

    @Test
    fun `when processing entry and relaying fails should set status to error`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData, SmsRelayStatus.PENDING)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)
        whenever(mockRelayService.relay(any())).thenThrow(RuntimeException("test"))

        subject.process(smsData)

        verify(mockRepository).update(
            eq(
                smsEntry.copy(
                    sendStatus = SmsRelayStatus.ERROR,
                    sendFailureReason = "java.lang.RuntimeException: test"
                ),
            ),
        )
    }

}