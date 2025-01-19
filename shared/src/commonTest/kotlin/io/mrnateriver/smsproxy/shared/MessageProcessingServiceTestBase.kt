package io.mrnateriver.smsproxy.shared

import arrow.core.left
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository
import io.mrnateriver.smsproxy.shared.contracts.MessageStatsService
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import io.mrnateriver.smsproxy.shared.services.MessageProcessingConfig
import io.mrnateriver.smsproxy.shared.services.MessageProcessingService
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

abstract class MessageProcessingServiceTestBase {
    protected val mockProcessingConfig = MessageProcessingConfig(7, 48.seconds)
    protected val mockRelayService = mock<MessageRelayService>()
    protected val mockRepository = mock<MessageRepository> {
        onBlocking { update(any<MessageEntry>()) }.then { it.arguments[0] }
    }
    protected val mockObservabilityService =
        mock<ObservabilityService> {
            onBlocking<ObservabilityService, Any> {
                runSpan(any<String>(), any<Map<String, String>>(), any<suspend () -> Unit>())
            } doSuspendableAnswer {
                it.getArgument<suspend () -> Any>(2)()
            }
        }
    protected val mockStatsService =
        mock<MessageStatsService> {
            onBlocking { incrementProcessingSuccesses() }.then { }
            onBlocking { incrementProcessingErrors() }.then { }
        }
    protected val mockClock = mock<Clock> { on(it.now()).thenReturn(Clock.System.now()) }

    protected val subject = MessageProcessingService(
        mockRepository,
        mockRelayService,
        mockObservabilityService,
        mockStatsService,
        mockProcessingConfig,
        mockClock,
    )

    protected fun createTestMessageData(receivedAt: Instant = mockClock.now()) =
        MessageData("123", receivedAt, "Hello, World!")

    protected fun createTestMessageEntry(
        messageData: MessageData,
        status: MessageRelayStatus = MessageRelayStatus.PENDING,
        retries: Int = 0,
        createdAt: Instant? = mockClock.now(),
        updatedAt: Instant? = mockClock.now(),
    ) =
        MessageEntry(
            UUID.randomUUID(),
            "123",
            status,
            retries,
            null,
            messageData.left(),
            createdAt,
            updatedAt,
        )
}
