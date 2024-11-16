package io.mrnateriver.smsproxy.relay.services.usecases

import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsEntry
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageStatsRepository as MessageStatsRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageStatsServiceTest {
    private val messagesRepository = mock<MessageRepositoryContract> {
        onBlocking { getCountByStatus(anyVararg(MessageRelayStatus::class)) } doAnswer { 42 }
        onBlocking { getLastEntryByStatus(anyVararg(MessageRelayStatus::class)) } doAnswer { createTestMessageEntry() }
        onBlocking { getCount() } doAnswer { 42 }
        onBlocking { getLastEntries(1) } doAnswer { listOf(createTestMessageEntry()) }
    }
    private val observabilityService =
        mock<ObservabilityServiceContract> {
            onBlocking<ObservabilityServiceContract, Any> {
                runSpan(any<String>(), any<suspend () -> Unit>())
            } doSuspendableAnswer { invocation ->
                invocation.getArgument<suspend () -> Any>(1)()
            }
        }
    private val messageStatsRepository = mock<MessageStatsRepositoryContract> {}

    private val now = Instant.fromEpochMilliseconds(1723996071981)
    private val nowLocal = now.toLocalDateTime(TimeZone.currentSystemDefault())

    private val subject = MessageStatsService(
        observabilityService,
        messageStatsRepository,
        messagesRepository,
    )

    @Test
    fun messageStatsService_shouldEmitUpdates() = runTest(timeout = 100.milliseconds) {
        // If statsUpdates does not emit without triggering it manually, the test would time out
        subject.statsUpdates.first()
        assertTrue(true)
    }

    @Test
    fun messageStatsService_shouldEmitUpdatesOnManualTrigger() = runTest {
        var emits = 0
        subject.statsUpdates.onEach { emits++ }.launchIn(CoroutineScope(Dispatchers.Unconfined))
        subject.triggerUpdate()
        assertEquals(2, emits)
    }

    @Test
    fun messageStatsService_shouldIncrementProcessingErrors() = runTest {
        subject.incrementProcessingErrors()

        verify(messageStatsRepository, times(1)).incrementProcessingErrors()
        verify(observabilityService, times(1)).incrementCounter(METRICS_NAME_PROCESSING_ERRORS)
    }

    @Test
    fun messageStatsService_shouldQueryProcessingFailures() = runTest {
        val statsData = subject.getProcessingFailures().first()
        assertEquals(42, statsData.value)
        assertEquals(nowLocal, statsData.lastEvent)
    }

    @Test
    fun messageStatsService_shouldQueryRelayedMessages() = runTest {
        val statsData = subject.getRelayedMessages().first()
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
        )

        var statsData = subject.getStats().first()
        assertEquals(0, statsData.errors.value)
        assertEquals(42, statsData.failures.value)
        assertEquals(42, statsData.processed.value)
        assertEquals(42, statsData.relayed.value)
        assertEquals(null, statsData.errors.lastEvent)
        assertEquals(nowLocal, statsData.failures.lastEvent)
        assertEquals(nowLocal, statsData.processed.lastEvent)
        assertEquals(nowLocal, statsData.relayed.lastEvent)

        subject.incrementProcessingErrors()

        whenever(messageStatsRepository.getProcessingErrors()).thenReturn(
            flowOf(MessageStatsEntry(42, nowLocal)),
        )

        statsData = subject.getStats().first()
        assertEquals(42, statsData.errors.value)
        assertEquals(42, statsData.failures.value)
        assertEquals(42, statsData.processed.value)
        assertEquals(42, statsData.relayed.value)
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
        MessageData("123", now, "Hello, World!"),
        now,
        now,
    )
}
