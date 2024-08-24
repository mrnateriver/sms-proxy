package io.mrnateriver.smsproxy.relay.services.usecases

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageProcessingScheduler as MessageProcessingSchedulerContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageStatsService as MessageStatsServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageReceiverServiceTest {
    private var smsProcessingService = mock<MessageProcessingServiceContract> {}
    private var statsService = mock<MessageStatsServiceContract> {}
    private val observabilityService =
        mock<ObservabilityServiceContract> {
            onBlocking<ObservabilityServiceContract, Any> {
                runSpan(any<String>(), any<suspend () -> Unit>())
            } doSuspendableAnswer {
                it.getArgument<suspend () -> Any>(1)()
            }
        }
    private var schedulerService = mock<MessageProcessingSchedulerContract> {}

    private val subject = MessageReceiverService(
        smsProcessingService,
        statsService,
        observabilityService,
        schedulerService,
    )

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun handleIncomingMessage_shouldRunSpan() = runTest {
        subject.handleIncomingMessage("sender", "hello")
        verify(observabilityService).runSpan(any<String>(), any())
    }

    @Test
    fun handleIncomingMessage_shouldIncrementFailuresOnException() = runTest {
        whenever(smsProcessingService.process(any())).thenThrow(RuntimeException())

        subject.handleIncomingMessage("sender", "hello")

        verify(statsService).incrementProcessingErrors()
    }

    @Test
    fun handleIncomingMessage_shouldScheduleBackgroundRetryOnException() = runTest {
        whenever(smsProcessingService.process(any())).thenThrow(RuntimeException())

        subject.handleIncomingMessage("sender", "hello")

        verify(schedulerService).scheduleBackgroundMessageProcessing()
    }

    @Test
    fun handleIncomingMessage_shouldTriggerStatsUpdate() = runTest {
        subject.handleIncomingMessage("sender", "hello")

        verify(statsService).triggerUpdate()
    }

    @Test
    fun handleIncomingMessage_shouldTriggerStatsUpdateOnFailure() = runTest {
        whenever(smsProcessingService.process(any())).thenThrow(RuntimeException())

        subject.handleIncomingMessage("sender", "hello")

        verify(statsService).triggerUpdate()
    }
}