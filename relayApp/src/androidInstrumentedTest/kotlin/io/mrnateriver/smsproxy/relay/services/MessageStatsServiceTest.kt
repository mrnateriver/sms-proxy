package io.mrnateriver.smsproxy.relay.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
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
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageStatsServiceTest {
    private val backgroundTestScope = CoroutineScope(Dispatchers.Unconfined)
    private val testContext: Context = ApplicationProvider.getApplicationContext()
    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = backgroundTestScope,
            produceFile = {
                testContext.preferencesDataStoreFile(
                    "store-${UUID.randomUUID()}"
                )
            },
        )
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
            } doSuspendableAnswer {
                it.getArgument<suspend () -> Any>(1)()
            }
        }
    private val now = Instant.fromEpochMilliseconds(1723996071981)
    private val nowLocal = now.toLocalDateTime(TimeZone.currentSystemDefault())
    private val mockClock = mock<Clock> { on(it.now()).thenReturn(now) }

    private val subject = MessageStatsService(
        testDataStore,
        observabilityService,
        messagesRepository,
        mockClock,
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
    fun messageStatsService_shouldIncrementProcessingFailures() = runTest {
        var statsData = subject.getProcessingErrors().first()
        assertEquals(0, statsData.value)
        assertEquals(null, statsData.lastEvent)

        subject.incrementProcessingErrors()
        statsData = subject.getProcessingErrors().first()
        assertEquals(1, statsData.value)
        assertEquals(nowLocal, statsData.lastEvent)
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
        var statsData = subject.getStats().first()
        assertEquals(0, statsData.errors)
        assertEquals(42, statsData.failures)
        assertEquals(42, statsData.processed)
        assertEquals(42, statsData.relayed)
        assertEquals(null, statsData.lastErrorAt)
        assertEquals(nowLocal, statsData.lastFailureAt)
        assertEquals(nowLocal, statsData.lastProcessedAt)
        assertEquals(nowLocal, statsData.lastRelayedAt)

        subject.incrementProcessingErrors()

        statsData = subject.getStats().first()
        assertEquals(1, statsData.errors)
        assertEquals(42, statsData.failures)
        assertEquals(42, statsData.processed)
        assertEquals(42, statsData.relayed)
        assertEquals(nowLocal, statsData.lastErrorAt)
        assertEquals(nowLocal, statsData.lastFailureAt)
        assertEquals(nowLocal, statsData.lastProcessedAt)
        assertEquals(nowLocal, statsData.lastRelayedAt)
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