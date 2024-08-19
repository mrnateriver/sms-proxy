package io.mrnateriver.smsproxy.relay.services.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class SettingsServiceTest {
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

    private val subject = SettingsService(testDataStore, backgroundTestScope)

    @Test
    fun settingsService_baseApiUrl_shouldReturnEmptyStringByDefault() = runTest {
        val actual = subject.baseApiUrl.first()
        assertEquals("", actual)
    }

    @Test
    fun settingsService_baseApiUrl_shouldEmitNewValueAfterSetting() = runTest {
        subject.setBaseApiUrl("http://example.com")
        assertEquals("http://example.com", subject.baseApiUrl.first())
    }

    @Test
    fun settingsService_receiverKey_shouldReturnEmptyStringByDefault() = runTest {
        val actual = subject.receiverKey.first()
        assertEquals("", actual)
    }

    @Test
    fun settingsService_receiverKey_shouldEmitNewValueAfterSetting() = runTest {
        subject.setReceiverKey("test1234test1234")
        assertEquals("test1234test1234", subject.receiverKey.first())
    }

    @Test
    fun settingsService_showRecentMessages_shouldReturnTrueByDefault() = runTest {
        val actual = subject.showRecentMessages.first()
        assertEquals(true, actual)
    }

    @Test
    fun settingsService_showRecentMessages_shouldEmitNewValueAfterSetting() = runTest {
        subject.setShowRecentMessages(false)
        assertEquals(false, subject.showRecentMessages.first())
    }

    @Test
    fun settingsService_isApiConfigured_shouldReturnFalseByDefault() = runTest {
        assertFalse(subject.isApiConfigured.first())
    }

    @Test
    fun settingsService_isApiConfigured_shouldReturnFalseIfOnlyOneOfRequiredParamsIsSet() =
        runTest {
            subject.setReceiverKey("test1234test1234")
            assertFalse(subject.isApiConfigured.first())

            subject.setReceiverKey("")
            subject.setBaseApiUrl("http://example.com")
            assertFalse(subject.isApiConfigured.first())
        }

    @Test
    fun settingsService_isApiConfigured_shouldReturnTrueIfUrlAndKeyAreSet() = runTest {
        subject.setReceiverKey("test1234test1234")
        subject.setBaseApiUrl("http://example.com")
        assertTrue(subject.isApiConfigured.first())
    }
}