package io.mrnateriver.smsproxy.shared

import io.mrnateriver.smsproxy.shared.mocks.MockObservabilityService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertSame

@OptIn(ExperimentalCoroutinesApi::class)
class SmsProcessingServiceTest {
//    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: SmsProcessingService

    private val mockRepository = mock<SmsRepository>()
    private val mockRelayService = mock<SmsRelayService>()
    private val mockObservabilityService =
        MockObservabilityService() // TODO: figure out a way to use Mockito

    @BeforeTest
    fun setup() {
        subject = SmsProcessingService(mockRepository, mockRelayService, mockObservabilityService)
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

        val returned = subject.process(smsData)

        assertSame(smsEntry, returned)
    }

    @Test
    fun `when processing entry should set its status to in progress`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)

        subject.process(smsData)

        verify(mockRepository).startProgress(smsEntry.guid)
    }

    @Test
    fun `when processing entry should relay it`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)

        subject.process(smsData)

        verify(mockRelayService).relay(smsEntry)
    }

    @Test
    fun `when processing entry should update its status to success`() = runTest {
        val smsData = createTestSmsData()
        val smsEntry = createTestSmsEntry(smsData)

        whenever(mockRepository.insert(any())).thenReturn(smsEntry)

        subject.process(smsData)

        verify(mockRepository).updateStatus(smsEntry.guid, SmsRelayStatus.SUCCESS)
    }

    // TODO: other tests

    private fun createTestSmsData(clock: Clock = Clock.System) =
        SmsData("123", clock.now().toLocalDateTime(TimeZone.UTC), "Hello, World!")

    private fun createTestSmsEntry(
        smsData: SmsData,
        status: SmsRelayStatus = SmsRelayStatus.PENDING,
        clock: Clock = Clock.System,
    ) =
        SmsEntry(UUID.randomUUID(), "123", status, 0u, null, smsData, clock.now(), null)
}