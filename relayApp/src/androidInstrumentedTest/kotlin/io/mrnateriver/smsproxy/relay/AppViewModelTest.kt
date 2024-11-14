package io.mrnateriver.smsproxy.relay

import io.mrnateriver.smsproxy.relay.services.data.ProxyApiCertificates
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageStatsService
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.SettingsService
import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsData
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.util.UUID
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract

class AppViewModelTest {
    private val now = Instant.fromEpochMilliseconds(1723996071981)

    @Test
    fun appViewModel_shouldShowApiKeyErrorIfItsNotSet() {
        val viewModel = AppViewModel(
            settingsService = mock<SettingsService>(),
            statsService = mock<MessageStatsService>(),
            messagesRepository = mock<MessageRepositoryContract>(),
            apiCertificates = ProxyApiCertificates(),
        )
        viewModel.apiKey = ""
        assertTrue(viewModel.showApiKeyError)

        viewModel.apiKey = "test"
        assertFalse(viewModel.showApiKeyError)
    }

    @Test
    fun appViewModel_shouldShowMissingCertificatesErrorIfAnyCertIsMissing() {
        assertTrue(
            AppViewModel(
                settingsService = mock<SettingsService>(),
                statsService = mock<MessageStatsService>(),
                messagesRepository = mock<MessageRepositoryContract>(),
                apiCertificates = ProxyApiCertificates(),
            ).showMissingCertificatesError,
        )

        assertTrue(
            AppViewModel(
                settingsService = mock<SettingsService>(),
                statsService = mock<MessageStatsService>(),
                messagesRepository = mock<MessageRepositoryContract>(),
                apiCertificates = ProxyApiCertificates(serverCertificatePem = "test"),
            ).showMissingCertificatesError,
        )

        assertTrue(
            AppViewModel(
                settingsService = mock<SettingsService>(),
                statsService = mock<MessageStatsService>(),
                messagesRepository = mock<MessageRepositoryContract>(),
                apiCertificates = ProxyApiCertificates(clientCertificatePem = "test"),
            ).showMissingCertificatesError,
        )

        assertTrue(
            AppViewModel(
                settingsService = mock<SettingsService>(),
                statsService = mock<MessageStatsService>(),
                messagesRepository = mock<MessageRepositoryContract>(),
                apiCertificates = ProxyApiCertificates(clientPrivateKeyPem = "test"),
            ).showMissingCertificatesError,
        )

        assertTrue(
            AppViewModel(
                settingsService = mock<SettingsService>(),
                statsService = mock<MessageStatsService>(),
                messagesRepository = mock<MessageRepositoryContract>(),
                apiCertificates = ProxyApiCertificates(
                    clientCertificatePem = "test",
                    clientPrivateKeyPem = "test",
                ),
            ).showMissingCertificatesError,
        )

        assertFalse(
            AppViewModel(
                settingsService = mock<SettingsService>(),
                statsService = mock<MessageStatsService>(),
                messagesRepository = mock<MessageRepositoryContract>(),
                apiCertificates = ProxyApiCertificates(
                    serverCertificatePem = "test",
                    clientCertificatePem = "test",
                    clientPrivateKeyPem = "test",
                ),
            ).showMissingCertificatesError,
        )
    }

    @Test
    fun appViewModel_shouldShowServerSettingsHintIfApiIsNotConfigured() = runTest {
        assertTrue(
            AppViewModel(
                settingsService = mock<SettingsService> {
                    on { isApiConfigured } doReturn flowOf(false)
                },
                statsService = mock<MessageStatsService>(),
                messagesRepository = mock<MessageRepositoryContract>(),
                apiCertificates = ProxyApiCertificates(),
            ).showServerSettingsHint.first(),
        )

        assertFalse(
            AppViewModel(
                settingsService = mock<SettingsService> {
                    on { isApiConfigured } doReturn flowOf(true)
                },
                statsService = mock<MessageStatsService>(),
                messagesRepository = mock<MessageRepositoryContract>(),
                apiCertificates = ProxyApiCertificates(),
            ).showServerSettingsHint.first(),
        )
    }

    @Test
    fun appViewModel_shouldEmitMessageStats() = runTest {
        val stubStatsData = MessageStatsData()
        val statsService = mock<MessageStatsService> {
            on { getStats() } doReturn flowOf(stubStatsData)
        }

        assertTrue(
            AppViewModel(
                settingsService = mock<SettingsService>(),
                statsService = statsService,
                messagesRepository = mock<MessageRepositoryContract>(),
                apiCertificates = ProxyApiCertificates(),
            ).messageStats.first() === stubStatsData,
        )
    }

    @Test
    fun appViewModel_shouldEmitEmptyListOfRecentMessagesIfDisabled() = runTest {
        val stubMessageRecords = listOf(
            createMessageEntry("test 1", "test 1"),
            createMessageEntry("test 2", "test 2"),
            createMessageEntry("test 3", "test 3"),
        )

        val settingsServiceMock = mock<SettingsService> {
            on { showRecentMessages } doReturn flowOf(false)
        }

        assertTrue(
            AppViewModel(
                settingsService = settingsServiceMock,
                statsService = mock<MessageStatsService>(),
                messagesRepository = mock<MessageRepositoryContract> {
                    onBlocking { getLastEntries(any()) } doReturn stubMessageRecords
                },
                apiCertificates = ProxyApiCertificates(),
            ).messageRecordsRecent.first().isEmpty(),
        )
    }

    @Test
    fun appViewModel_shouldEmitRecentMessagesIfEnabled() = runTest {
        val stubMessageRecords = listOf(
            createMessageEntry("test 1", "test 1"),
            createMessageEntry("test 2", "test 2"),
            createMessageEntry("test 3", "test 3"),
        )

        val emittedRecords = AppViewModel(
            settingsService = mock<SettingsService> {
                on { showRecentMessages } doReturn flowOf(true)
            },
            statsService = mock<MessageStatsService> {
                on { statsUpdates } doReturn flowOf(Unit)
            },
            messagesRepository = mock<MessageRepositoryContract> {
                onBlocking { getLastEntries(any()) } doReturn stubMessageRecords
            },
            apiCertificates = ProxyApiCertificates(),
        ).messageRecordsRecent.first()

        assertTrue(emittedRecords.isNotEmpty())
        assertTrue(emittedRecords.size == stubMessageRecords.size)
        assertTrue(emittedRecords[0] == stubMessageRecords[0])
        assertTrue(emittedRecords[1] == stubMessageRecords[1])
        assertTrue(emittedRecords[2] == stubMessageRecords[2])
    }

    private fun createMessageEntry(
        sender: String,
        message: String,
        status: MessageRelayStatus = MessageRelayStatus.SUCCESS,
    ): MessageEntry {
        return MessageEntry(
            guid = UUID.randomUUID(),
            externalId = null,
            messageData = MessageData(
                sender = sender,
                message = message,
                receivedAt = now,
            ),
            sendStatus = status,
            sendRetries = 1,
            sendFailureReason = null,
            updatedAt = now,
            createdAt = now,
        )
    }
}
