package io.mrnateriver.smsproxy.shared

import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.time.Duration.Companion.seconds

abstract class MessageProcessingServiceTestBase {
    protected lateinit var subject: MessageProcessingService

    protected val mockProcessingConfig = MessageProcessingConfig(7u, 48.seconds)
    protected val mockRelayService = mock<MessageRelayService>()
    protected val mockRepository = mock<MessageRepository> {
        onBlocking { update(any<MessageEntry>()) }.then { it.arguments[0] }
    }
    protected val mockObservabilityService =
        mock<ObservabilityService> {
            onBlocking<ObservabilityService, Any> {
                runSpan(any<String>(), any<suspend () -> Unit>())
            } doSuspendableAnswer {
                it.getArgument<suspend () -> Any>(1)()
            }
        }

    protected val mockClock = mock<Clock> {
        on(it.now()).then { Clock.System.now() }
    }

    @BeforeTest
    fun setup() {
        subject = MessageProcessingService(
            mockRepository,
            mockRelayService,
            mockObservabilityService,
            mockProcessingConfig,
            mockClock,
        )
    }

    @AfterTest
    fun tearDown() {
        reset(mockRepository, mockRelayService, mockObservabilityService)
    }

    protected fun createTestMessageData(clock: Clock = Clock.System) =
        MessageData("123", clock.now(), "Hello, World!")

    protected fun createTestMessageEntry(
        messageData: MessageData,
        status: MessageRelayStatus = MessageRelayStatus.PENDING,
        retries: UShort = 0u,
        createdAt: Instant? = Clock.System.now(),
        updatedAt: Instant? = Clock.System.now(),
    ) =
        MessageEntry(
            UUID.randomUUID(),
            "123",
            status,
            retries,
            null,
            messageData,
            createdAt,
            updatedAt
        )

}