package io.mrnateriver.smsproxy.relay.services

import android.content.Intent
import android.telephony.SmsMessage
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.mrnateriver.smsproxy.shared.contracts.MessageProcessingService as MessageProcessingServiceContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class SmsBroadcastReceiverServiceTest {
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
    private var workerService = mock<MessageProcessingWorkerServiceContract> {}
    private var smsIntentParserService = mock<SmsIntentParserServiceContract> {
        onBlocking { getMessagesFromIntent(any()) }.thenReturn(emptyArray())
    }

    private val subject = SmsBroadcastReceiverService(
        smsProcessingService,
        statsService,
        observabilityService,
        workerService,
        smsIntentParserService,
    )

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun handleIncomingMessagesIntent_shouldRunSpan() = runTest {
        subject.handleIncomingMessagesIntent(rule.activity.applicationContext, Intent())
        verify(observabilityService).runSpan(any<String>(), any())
    }

    @Test
    fun handleIncomingMessagesIntent_shouldCallSmsIntentParserService() = runTest {
        val intent = Intent()
        subject.handleIncomingMessagesIntent(rule.activity.applicationContext, intent)
        verify(smsIntentParserService).getMessagesFromIntent(intent)
    }

    @Test
    fun handleIncomingMessagesIntent_shouldNotProcessMessagesIfNone() = runTest {
        subject.handleIncomingMessagesIntent(rule.activity.applicationContext, Intent())
        verify(smsProcessingService, never()).process(any())
    }

    @Test
    fun handleIncomingMessagesIntent_shouldConcatenateMultipleMessages() = runTest {
        val intent = Intent()
        val messages = arrayOf(
            mockSmsMessage("message1", "sender1"),
            mockSmsMessage("message2", "sender2"),
        )

        whenever(smsIntentParserService.getMessagesFromIntent(intent)).thenReturn(messages)

        subject.handleIncomingMessagesIntent(rule.activity.applicationContext, intent)

        verify(smsProcessingService).process(
            argThat { sender == "sender1" && message == "message1message2" }
        )
    }

    @Test
    fun handleIncomingMessagesIntent_shouldIncrementFailuresOnException() = runTest {
        val intent = Intent()
        val message = mockSmsMessage("message", "sender")

        whenever(smsIntentParserService.getMessagesFromIntent(intent)).thenReturn(arrayOf(message))
        whenever(smsProcessingService.process(any())).thenThrow(RuntimeException())

        subject.handleIncomingMessagesIntent(rule.activity.applicationContext, intent)

        verify(statsService).incrementProcessingErrors()
    }

    @Test
    fun handleIncomingMessagesIntent_shouldScheduleBackgroundRetryOnException() = runTest {
        val intent = Intent()
        val message = mockSmsMessage("message", "sender")

        whenever(smsIntentParserService.getMessagesFromIntent(intent)).thenReturn(arrayOf(message))
        whenever(smsProcessingService.process(any())).thenThrow(RuntimeException())

        subject.handleIncomingMessagesIntent(rule.activity.applicationContext, intent)

        verify(workerService).scheduleBackgroundWork(rule.activity.applicationContext)
    }

    @Test
    fun handleIncomingMessagesIntent_shouldTriggerStatsUpdate() = runTest {
        val intent = Intent()
        val message = mockSmsMessage("message", "sender")

        whenever(smsIntentParserService.getMessagesFromIntent(intent)).thenReturn(arrayOf(message))

        subject.handleIncomingMessagesIntent(rule.activity.applicationContext, intent)

        verify(statsService).triggerUpdate()
    }

    @Test
    fun handleIncomingMessagesIntent_shouldTriggerStatsUpdateOnFailure() = runTest {
        val intent = Intent()
        val message = mockSmsMessage("message", "sender")

        whenever(smsIntentParserService.getMessagesFromIntent(intent)).thenReturn(arrayOf(message))
        whenever(smsProcessingService.process(any())).thenThrow(RuntimeException())

        subject.handleIncomingMessagesIntent(rule.activity.applicationContext, intent)

        verify(statsService).triggerUpdate()
    }

    private fun mockSmsMessage(message: String, sender: String): SmsMessage {
        return mock {
            on { displayMessageBody }.thenReturn(message)
            on { originatingAddress }.thenReturn(sender)
        }
    }
}