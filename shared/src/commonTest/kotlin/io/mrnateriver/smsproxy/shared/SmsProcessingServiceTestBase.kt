package io.mrnateriver.smsproxy.shared

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

abstract class SmsProcessingServiceTestBase {
    protected lateinit var subject: SmsProcessingService

    protected val mockProcessingConfig = SmsProcessingConfig(7u, 48.seconds)
    protected val mockRelayService = mock<SmsRelayService>()
    protected val mockRepository = mock<SmsRepository> {
        onBlocking { update(any<SmsEntry>()) }.then { it.arguments[0] }
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
        subject = SmsProcessingService(
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

    protected fun createTestSmsData(clock: Clock = Clock.System) =
        SmsData("123", clock.now(), "Hello, World!")

    protected fun createTestSmsEntry(
        smsData: SmsData,
        status: SmsRelayStatus = SmsRelayStatus.PENDING,
        retries: UShort = 0u,
        createdAt: Instant? = Clock.System.now(),
        updatedAt: Instant? = Clock.System.now(),
    ) =
        SmsEntry(UUID.randomUUID(), "123", status, retries, null, smsData, createdAt, updatedAt)

}