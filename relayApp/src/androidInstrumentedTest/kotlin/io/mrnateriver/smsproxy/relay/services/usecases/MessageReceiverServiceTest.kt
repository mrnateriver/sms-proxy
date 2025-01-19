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
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class MessageReceiverServiceTest {
    private val smsProcessingService = mock<MessageProcessingServiceContract> {}
    private val observabilityService =
        mock<ObservabilityServiceContract> {
            onBlocking<ObservabilityServiceContract, Any> {
                runSpan(any<String>(), any<Map<String, String>>(), any<suspend () -> Unit>())
            } doSuspendableAnswer { invocation ->
                invocation.getArgument<suspend () -> Any>(2)()
            }
        }
    private val schedulerService = mock<MessageProcessingSchedulerContract> {}

    private val subject = MessageReceiverService(
        smsProcessingService,
        observabilityService,
        schedulerService,
    )

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun handleIncomingMessage_shouldRunSpan() = runTest {
        subject.handleIncomingMessage("sender", "hello")
        verify(observabilityService).runSpan(any<String>(), any(), any())
    }

    @Test
    fun handleIncomingMessage_shouldScheduleBackgroundRetryOnException() = runTest {
        whenever(smsProcessingService.process(any())).thenThrow(RuntimeException())

        subject.handleIncomingMessage("sender", "hello")

        verify(schedulerService).scheduleBackgroundMessageProcessing()
    }
}
