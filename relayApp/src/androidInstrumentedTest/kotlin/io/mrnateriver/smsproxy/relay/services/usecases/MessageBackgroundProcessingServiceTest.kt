package io.mrnateriver.smsproxy.relay.services.usecases

import arrow.core.left
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageBackgroundProcessingService.MessageBackgroundProcessingResult.FAILURE
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageBackgroundProcessingService.MessageBackgroundProcessingResult.RETRY
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageBackgroundProcessingService.MessageBackgroundProcessingResult.SUCCESS
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageBackgroundProcessingServiceTest {
    private val processingService = mock<MessageProcessingServiceContract> {
        onBlocking { handleUnprocessedMessages() }.thenReturn(emptyList())
    }
    private val observabilityService =
        mock<ObservabilityServiceContract> {
            onBlocking<ObservabilityServiceContract, Any> {
                runSpan(any<String>(), any<Map<String, String>>(), any<suspend () -> Unit>())
            } doSuspendableAnswer { invocation ->
                invocation.getArgument<suspend () -> Any>(2)()
            }
        }

    private val subject =
        MessageBackgroundProcessingService(
            processingService,
            observabilityService,
        )

    private val now = Instant.fromEpochMilliseconds(1723996071981)

    @Test
    fun handleUnprocessedMessages_shouldRunASpan() = runTest {
        subject.handleUnprocessedMessages()
        verify(observabilityService).runSpan(any<String>(), any(), any())
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
        assertEquals(RETRY, result)
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
        assertEquals(SUCCESS, result)
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
        assertEquals(FAILURE, result)
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
            MessageData("123", now, "Hello, World!").left(),
            now,
            now,
        )
}
