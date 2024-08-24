package io.mrnateriver.smsproxy.relay.services.usecases

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.SettingsRepository as SettingsRepositoryContract

class SettingsServiceTest {
    @Test
    fun settingsService_baseApiUrl_shouldReturnValueFromRepository() = runTest {
        val settingsRepositoryMock = mock<SettingsRepositoryContract> {
            on { baseApiUrl } doReturn flowOf("test")
        }
        val subject = SettingsService(settingsRepositoryMock)

        val actual = subject.baseApiUrl.first()
        assertEquals("test", actual)
    }

    @Test
    fun settingsService_baseApiUrl_shouldSetNewValueInRepository() = runTest {
        val settingsRepositoryMock = mock<SettingsRepositoryContract> {}
        val subject = SettingsService(settingsRepositoryMock)

        subject.setBaseApiUrl("http://example.com")
        verify(settingsRepositoryMock, times(1)).setBaseApiUrl("http://example.com")
    }

    @Test
    fun settingsService_receiverKey_shouldReturnValueFromRepository() = runTest {
        val settingsRepositoryMock = mock<SettingsRepositoryContract> {
            on { receiverKey } doReturn flowOf("test")
        }
        val subject = SettingsService(settingsRepositoryMock)

        val actual = subject.receiverKey.first()
        assertEquals("test", actual)
    }

    @Test
    fun settingsService_receiverKey_shouldSetNewValueInRepository() = runTest {
        val settingsRepositoryMock = mock<SettingsRepositoryContract> {}
        val subject = SettingsService(settingsRepositoryMock)

        subject.setReceiverKey("http://example.com")
        verify(settingsRepositoryMock, times(1)).setReceiverKey("http://example.com")
    }

    @Test
    fun settingsService_showRecentMessages_shouldReturnValueFromRepository() = runTest {
        val settingsRepositoryMock = mock<SettingsRepositoryContract> {
            on { showRecentMessages } doReturn flowOf(true)
        }
        val subject = SettingsService(settingsRepositoryMock)

        val actual = subject.showRecentMessages.first()
        assertEquals(true, actual)
    }

    @Test
    fun settingsService_showRecentMessages_shouldSetNewValueInRepository() = runTest {
        val settingsRepositoryMock = mock<SettingsRepositoryContract> {}
        val subject = SettingsService(settingsRepositoryMock)

        subject.setShowRecentMessages(true)
        verify(settingsRepositoryMock, times(1)).setShowRecentMessages(true)
    }

    @Test
    fun settingsService_isApiConfigured_shouldReturnFalseIfOnlyOneOfRequiredParamsIsSet() =
        runTest {
            val settingsRepositoryMock = mock<SettingsRepositoryContract> {
                on { receiverKey } doReturn flowOf("test1234test1234")
                on { baseApiUrl } doReturn flowOf("")
            }
            val subject1 = SettingsService(settingsRepositoryMock)
            assertFalse(subject1.isApiConfigured.first())

            whenever(settingsRepositoryMock.baseApiUrl).thenReturn(flowOf("https://example.com"))
            whenever(settingsRepositoryMock.receiverKey).thenReturn(flowOf(""))

            val subject2 = SettingsService(settingsRepositoryMock)
            assertFalse(subject2.isApiConfigured.first())
        }

    @Test
    fun settingsService_isApiConfigured_shouldReturnTrueIfUrlAndKeyAreSet() = runTest {
        val settingsRepositoryMock = mock<SettingsRepositoryContract> {
            on { receiverKey } doReturn flowOf("test1234test1234")
            on { baseApiUrl } doReturn flowOf("https://example.com")
        }
        val subject = SettingsService(settingsRepositoryMock)
        assertTrue(subject.isApiConfigured.first())
    }
}