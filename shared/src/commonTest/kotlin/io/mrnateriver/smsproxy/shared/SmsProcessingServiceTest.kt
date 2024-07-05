package io.mrnateriver.smsproxy.shared

import io.mrnateriver.smsproxy.shared.mocks.MockObservabilityService
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.atMost
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.time.Duration.Companion.seconds

class SmsProcessingServiceTest {
    private lateinit var subject: SmsProcessingService

    private val testProcessingConfig = SmsProcessingConfig(7u, 48.seconds)
    private val mockRepository = mock<SmsRepository>()
    private val mockRelayService = mock<SmsRelayService>()
    private val mockObservabilityService =
        MockObservabilityService() // TODO: figure out a way to use Mockito
    private val mockClock = mock<Clock> {
        on(it.now()).then { Clock.System.now() }
    }

    @BeforeTest
    fun setup() {
        subject = SmsProcessingService(
            mockRepository,
            mockRelayService,
            mockObservabilityService,
            testProcessingConfig,
            mockClock,
        )
    }

    @AfterTest
    fun tearDown() {
        Mockito.reset(mockRepository)
        Mockito.reset(mockRelayService)
        mockObservabilityService.reset()
    }

    @Test
    fun `when processing entry should save it to the repository`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)
        whenever(mockRepository.getById(smsEntry.guid)).thenReturn(smsEntry.copy())

        subject.process(smsData)

        verify(mockRepository, times(1)).insert(smsData)
    }

    @Test
    fun `when processing entry should set its status to in progress`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)
        whenever(mockRepository.getById(smsEntry.guid)).thenReturn(smsEntry.copy())

        subject.process(smsData)

        verify(mockRepository).incrementRetriesAndStartProgress(smsEntry.guid)
    }

    @Test
    fun `when processing entry should relay it`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)
        whenever(mockRepository.getById(smsEntry.guid)).thenReturn(smsEntry.copy())

        subject.process(smsData)

        verify(mockRelayService, atMost(1)).relay(smsEntry)
    }

    @Test
    fun `when processing entry should update its status to success`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)
        whenever(mockRepository.getById(smsEntry.guid)).thenReturn(smsEntry.copy())

        subject.process(smsData)

        verify(mockRepository).updateStatus(smsEntry.guid, SmsRelayStatus.SUCCESS)
    }

    // FIXME: these tests only concern handleUnprocessedMessages, which is not tested here yet
//    @Test
//    fun `when processing entry that's already been relayed should not relay it again`() = runTest {
//        val smsData = createTestSmsData()
//        val smsEntry = createTestSmsEntry(smsData, SmsRelayStatus.SUCCESS)
//
//        whenever(mockRepository.insert(any())).thenReturn(smsEntry)
//        whenever(mockRepository.getById(smsEntry.guid)).thenReturn(smsEntry.copy())
//
//        subject.process(smsData)
//
//        verify(mockRelayService, never()).relay(smsEntry)
//    }
//
//    @Test
//    fun `when processing entry that's failed should not relay it again`() = runTest {
//        val smsData = createTestSmsData()
//        val smsEntry = createTestSmsEntry(smsData, SmsRelayStatus.FAILED)
//
//        whenever(mockRepository.insert(any())).thenReturn(smsEntry)
//        whenever(mockRepository.getById(smsEntry.guid)).thenReturn(smsEntry.copy())
//
//        subject.process(smsData)
//
//        verify(mockRelayService, never()).relay(smsEntry)
//    }

    @Test
    fun `when processing entry should re-fetch entry from repository`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)
        whenever(mockRepository.getById(smsEntry.guid)).thenReturn(smsEntry.copy())

        val result = subject.process(smsData)

        verify(mockRepository).getById(smsEntry.guid)
        assertNotSame(smsEntry, result)
    }

    // FIXME: these tests only concern handleUnprocessedMessages, which is not tested here yet
//    @Test
//    fun `when processing entry that's in progress should not relay it`() = runTest {
//        val smsData = createTestSmsData()
//        val smsEntry = createTestSmsEntry(smsData, SmsRelayStatus.IN_PROGRESS)
//
//        whenever(mockRepository.insert(any())).thenReturn(smsEntry)
//        whenever(mockRepository.getById(smsEntry.guid)).thenReturn(smsEntry.copy())
//
//        subject.process(smsData)
//
//        verify(mockRelayService, never()).relay(smsEntry)
//    }
//
//    @Test
//    fun `when processing entry that's been retried too many times should mark it as failed`() =
//        runTest {
//            val smsData = createTestSmsData()
//            val smsEntry = createTestSmsEntry(smsData, SmsRelayStatus.PENDING, 7u)
//
//            whenever(mockRepository.insert(any())).thenReturn(smsEntry)
//            whenever(mockRepository.getById(smsEntry.guid)).thenReturn(smsEntry.copy())
//
//            subject.process(smsData)
//
//            verify(mockRelayService, never()).relay(smsEntry)
//            verify(mockRepository).updateStatus(smsEntry.guid, SmsRelayStatus.FAILED, any<String>())
//        }

    @Test
    fun `when processing entry and relaying fails should set status to error`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData, SmsRelayStatus.PENDING)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)
        whenever(mockRepository.getById(smsEntry.guid)).thenReturn(smsEntry.copy())
        whenever(mockRelayService.relay(any())).thenThrow(RuntimeException("test"))

        subject.process(smsData)

        verify(mockRepository).updateStatus(
            eq(smsEntry.guid),
            eq(SmsRelayStatus.ERROR),
            any<String>()
        )
    }

    // FIXME: these tests only concern handleUnprocessedMessages, which is not tested here yet
//    @Test
//    fun `when processing entry that's been stuck in progress should abort the relaying and record error`() =
//        runTest {
//            val smsData = createTestSmsData()
//            val smsEntry = createTestSmsEntry(
//                smsData,
//                SmsRelayStatus.IN_PROGRESS,
//                1u,
//                Instant.fromEpochMilliseconds(0),
//                Instant.fromEpochSeconds(10),
//            )
//
//            whenever(mockClock.now()).thenReturn(Instant.fromEpochSeconds((10.seconds + testProcessingConfig.timeout).inWholeSeconds))
//            whenever(mockRepository.insert(any())).thenReturn(smsEntry)
//            whenever(mockRepository.getById(smsEntry.guid)).thenReturn(smsEntry.copy())
//
//            subject.process(smsData)
//
//            verify(mockRepository).updateStatus(smsEntry.guid, SmsRelayStatus.ERROR, any<String>())
//        }


    private fun createTestSmsData(clock: Clock = Clock.System) =
        SmsData("123", clock.now().toLocalDateTime(TimeZone.UTC), "Hello, World!")

    private fun createTestSmsEntry(
        smsData: SmsData,
        status: SmsRelayStatus = SmsRelayStatus.PENDING,
        retries: UShort = 0u,
        createdAt: Instant? = Clock.System.now(),
        updatedAt: Instant? = Clock.System.now(),
    ) =
        SmsEntry(UUID.randomUUID(), "123", status, retries, null, smsData, createdAt, updatedAt)
}