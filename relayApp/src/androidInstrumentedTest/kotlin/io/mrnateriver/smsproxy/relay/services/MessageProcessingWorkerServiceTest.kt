package io.mrnateriver.smsproxy.relay.services

import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageProcessingWorkerServiceTest {
    private val processingService = mock<MessageProcessingServiceContract> {
        onBlocking { handleUnprocessedMessages() }.thenReturn(emptyList())
    }
    private val statsService = mock<MessageStatsServiceContract> {}
    private val observabilityService =
        mock<ObservabilityServiceContract> {
            onBlocking<ObservabilityServiceContract, Any> {
                runSpan(any<String>(), any<suspend () -> Unit>())
            } doSuspendableAnswer {
                it.getArgument<suspend () -> Any>(1)()
            }
        }

    private val subject =
        MessageProcessingWorkerService(processingService, statsService, observabilityService)

    private val now = Instant.fromEpochMilliseconds(1723996071981)

    @Test
    fun handleUnprocessedMessages_shouldRunASpan() = runTest {
        subject.handleUnprocessedMessages()
        verify(observabilityService).runSpan(any<String>(), any())
    }

    @Test
    fun handleUnprocessedMessages_shouldCallProcessingService() = runTest {
        subject.handleUnprocessedMessages()
        verify(processingService).handleUnprocessedMessages()
    }

    @Test
    fun handleUnprocessedMessages_shouldReturnRetryIfAnyError() = runTest {
        whenever(processingService.handleUnprocessedMessages()).thenReturn(
            listOf(
                createTestMessageEntry(MessageRelayStatus.ERROR),
                createTestMessageEntry(MessageRelayStatus.FAILED),
                createTestMessageEntry(MessageRelayStatus.ERROR),
                createTestMessageEntry(MessageRelayStatus.SUCCESS),
            ),
        )

        val result = subject.handleUnprocessedMessages()
        assertEquals(MessageProcessingWorkerResult.RETRY, result)
    }

    @Test
    fun handleUnprocessedMessages_shouldReturnSuccessIfAnySuccessAmongFailures() = runTest {
        whenever(processingService.handleUnprocessedMessages()).thenReturn(
            listOf(
                createTestMessageEntry(MessageRelayStatus.FAILED),
                createTestMessageEntry(MessageRelayStatus.FAILED),
                createTestMessageEntry(MessageRelayStatus.SUCCESS),
            ),
        )

        val result = subject.handleUnprocessedMessages()
        assertEquals(MessageProcessingWorkerResult.SUCCESS, result)
    }

    @Test
    fun handleUnprocessedMessages_shouldReturnFailureIfNoSuccess() = runTest {
        whenever(processingService.handleUnprocessedMessages()).thenReturn(
            listOf(
                createTestMessageEntry(MessageRelayStatus.FAILED),
                createTestMessageEntry(MessageRelayStatus.FAILED),
                createTestMessageEntry(MessageRelayStatus.PENDING),
            ),
        )

        val result = subject.handleUnprocessedMessages()
        assertEquals(MessageProcessingWorkerResult.FAILURE, result)
    }

    @Test
    fun handleUnprocessedMessages_shouldIncrementProcessingFailuresForAllErrors() = runTest {
        whenever(processingService.handleUnprocessedMessages()).thenReturn(
            listOf(
                createTestMessageEntry(MessageRelayStatus.ERROR),
                createTestMessageEntry(MessageRelayStatus.FAILED),
                createTestMessageEntry(MessageRelayStatus.ERROR),
                createTestMessageEntry(MessageRelayStatus.SUCCESS),
            ),
        )

        subject.handleUnprocessedMessages()
        verify(statsService, times(2)).incrementProcessingErrors()
    }

    @Test
    fun handleUnprocessedMessages_shouldCallStatsServiceToTriggerDownstreamUpdates() = runTest {
        subject.handleUnprocessedMessages()
        verify(statsService).triggerUpdate()
    }

    private fun createTestMessageEntry(
        status: MessageRelayStatus = MessageRelayStatus.PENDING,
    ) =
        MessageEntry(
            UUID.randomUUID(),
            "123",
            status,
            0,
            null,
            MessageData("123", now, "Hello, World!"),
            now,
            now,
        )
}