package io.mrnateriver.smsproxy.relay.services.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import java.util.UUID
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageStatsRepositoryTest {
    private val backgroundTestScope = CoroutineScope(Dispatchers.Unconfined)
    private val testContext: Context = ApplicationProvider.getApplicationContext()
    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            scope = backgroundTestScope,
            produceFile = {
                testContext.preferencesDataStoreFile(
                    "store-${UUID.randomUUID()}",
                )
            },
        )
    private val observabilityService =
        mock<ObservabilityServiceContract> {
            onBlocking<ObservabilityServiceContract, Any> {
                runSpan(any<String>(), any<suspend () -> Unit>())
            } doSuspendableAnswer { invocation ->
                invocation.getArgument<suspend () -> Any>(1)()
            }
        }
    private val now = Instant.fromEpochMilliseconds(1723996071981)
    private val nowLocal = now.toLocalDateTime(TimeZone.currentSystemDefault())
    private val mockClock = mock<Clock> { on(it.now()).thenReturn(now) }

    private val subject = MessageStatsRepository(
        observabilityService,
        testDataStore,
        mockClock,
    )

    @Test
    fun messageStatsService_shouldIncrementProcessingErrors() = runTest {
        var statsData = subject.getProcessingErrors().first()
        assertEquals(0, statsData.value)
        assertEquals(null, statsData.lastEvent)

        subject.incrementProcessingErrors()
        statsData = subject.getProcessingErrors().first()
        assertEquals(1, statsData.value)
        assertEquals(nowLocal, statsData.lastEvent)
    }

    @Test
    fun messageStatsService_shouldIncrementProcessingErrorsInDataStore() = runTest {
        subject.incrementProcessingErrors()
        subject.incrementProcessingErrors()
        subject.incrementProcessingErrors()

        val prefs = testDataStore.data.first().asMap()
        assertEquals(3, prefs[KEY_PROCESSING_ERRORS])
        assertEquals(now.toEpochMilliseconds(), prefs[KEY_PROCESSING_ERROR_TIMESTAMP])
    }

    @Test
    fun messageStatsService_shouldGetProcessingErrorsFromDataStore() = runTest {
        testDataStore.edit { prefs ->
            prefs[KEY_PROCESSING_ERRORS] = 42
            prefs[KEY_PROCESSING_ERROR_TIMESTAMP] = now.toEpochMilliseconds()
        }

        val statsData = subject.getProcessingErrors().first()
        assertEquals(42, statsData.value)
        assertEquals(nowLocal, statsData.lastEvent)
    }
}
