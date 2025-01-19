package io.mrnateriver.smsproxy.relay.services.usecases

import arrow.core.left
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageWatchService
import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsEntry
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyVararg
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.kotlin.wheneverBlocking
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageStatsRepository as MessageStatsRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageStatsServiceTest {
    private val messagesRepository = mock<MessageRepositoryContract> {
        onBlocking { getLastEntries(1) } doAnswer { listOf(createTestMessageEntry()) }
        onBlocking { getLastEntryByStatus(anyVararg(MessageRelayStatus::class)) } doAnswer { createTestMessageEntry() }
        onBlocking { getCountByStatus(anyVararg(MessageRelayStatus::class)) } doAnswer { 42 }
        onBlocking { getCount() } doAnswer { 42 }
    }
    private val observabilityService =
        mock<ObservabilityServiceContract> {
            onBlocking<ObservabilityServiceContract, Any> {
                runSpan(any<String>(), any<Map<String, String>>(), any<suspend () -> Unit>())
            } doSuspendableAnswer { invocation ->
                invocation.getArgument<suspend () -> Any>(2)()
            }
        }
    private val messageStatsRepository = mock<MessageStatsRepositoryContract> {
        onBlocking { getProcessingErrors() } doAnswer { flowOf(MessageStatsEntry(42, nowLocal)) }
        onBlocking { getProcessingSuccesses() } doAnswer { flowOf(MessageStatsEntry(123, nowLocal)) }
    }
    private val messagesWatchService = mock<MessageWatchService> {
        on { watchLastEntries(any<Int>()) } doAnswer { emptyFlow() }
    }

    private val now = Instant.fromEpochMilliseconds(1723996071981)
    private val nowLocal = now.toLocalDateTime(TimeZone.currentSystemDefault())

    private val subject = MessageStatsService(
        observabilityService,
        messageStatsRepository,
        messagesRepository,
        messagesWatchService,
    )

    @Test
    fun messageStatsService_shouldEmitUpdates() = runTest(timeout = 100.milliseconds) {
        // If statsUpdates does not emit without triggering it manually, the test would time out
        subject.getStats().first()
        assertTrue(true)
    }

    @Test
    fun messageStatsService_shouldEmitUpdatesOnEntriesUpdates() = runTest {
        wheneverBlocking { messagesWatchService.watchLastEntries(any()) }.thenReturn(flowOf(emptyList()))

        var emissions = 0
        subject.getStats().take(2).collect { emissions++ }

        assertEquals(2, emissions)
    }

    @Test
    fun messageStatsService_shouldIncrementProcessingErrors() = runTest {
        subject.incrementProcessingErrors()

        verify(messageStatsRepository, times(1)).incrementProcessingErrors()
        verify(observabilityService, times(1)).incrementCounter(METRICS_NAME_PROCESSING_ERRORS)
    }

    @Test
    fun messageStatsService_shouldIncrementProcessingSuccesses() = runTest {
        subject.incrementProcessingSuccesses()

        verify(messageStatsRepository, times(1)).incrementProcessingSuccesses()
        verify(observabilityService, times(1)).incrementCounter(METRICS_NAME_PROCESSING_SUCCESSES)
    }

    @Test
    fun messageStatsService_shouldQueryProcessingFailures() = runTest {
        val statsData = subject.getProcessingFailures().first()
        assertEquals(42, statsData.value)
        assertEquals(nowLocal, statsData.lastEvent)
    }

    @Test
    fun messageStatsService_shouldQueryProcessedMessages() = runTest {
        val statsData = subject.getProcessedMessages().first()
        assertEquals(42, statsData.value)
        assertEquals(nowLocal, statsData.lastEvent)
    }

    @Test
    fun messageStatsService_shouldEmitStatsData() = runTest {
        whenever(messageStatsRepository.getProcessingErrors()).thenReturn(
            flowOf(MessageStatsEntry()),
            flowOf(MessageStatsEntry(42, nowLocal)),
        )
        whenever(messageStatsRepository.getProcessingSuccesses()).thenReturn(
            flowOf(MessageStatsEntry()),
            flowOf(MessageStatsEntry(123, nowLocal)),
        )

        var statsData = subject.getStats().first()
        assertEquals(0, statsData.errors.value)
        assertEquals(42, statsData.failures.value)
        assertEquals(42, statsData.processed.value)
        assertEquals(0, statsData.relayed.value)
        assertEquals(null, statsData.errors.lastEvent)
        assertEquals(nowLocal, statsData.failures.lastEvent)
        assertEquals(nowLocal, statsData.processed.lastEvent)
        assertEquals(null, statsData.relayed.lastEvent)

        subject.incrementProcessingSuccesses() // Both errors and successes trigger an update

        statsData = subject.getStats().first()
        assertEquals(42, statsData.errors.value)
        assertEquals(42, statsData.failures.value)
        assertEquals(42, statsData.processed.value)
        assertEquals(123, statsData.relayed.value)
        assertEquals(nowLocal, statsData.errors.lastEvent)
        assertEquals(nowLocal, statsData.failures.lastEvent)
        assertEquals(nowLocal, statsData.processed.lastEvent)
        assertEquals(nowLocal, statsData.relayed.lastEvent)
    }

    private fun createTestMessageEntry() = MessageEntry(
        UUID.randomUUID(),
        "123",
        MessageRelayStatus.SUCCESS,
        0,
        null,
        MessageData("123", now, "Hello, World!").left(),
        now,
        now,
    )
}
