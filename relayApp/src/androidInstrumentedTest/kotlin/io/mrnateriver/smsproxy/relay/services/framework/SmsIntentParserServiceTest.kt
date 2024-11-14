package io.mrnateriver.smsproxy.relay.services.framework

import android.content.Intent
import android.telephony.SmsMessage
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class SmsIntentParserServiceTest {
    @Test
    fun getMessagesFromIntent_emptyIntent_shouldReturnNull() {
        val subject = SmsIntentParserService()
        val intent = Intent()
        val actual = subject.getMessagesFromIntent(intent)
        assertEquals(null, actual)
    }

    @Test
    fun getMessagesFromIntent_intentWithMessages_shouldConcatenateMessageBodies() {
        val subject = SmsIntentParserService(
            intentParser = {
                arrayOf(
                    mockSmsMessage("msg1", "sender1"),
                    mockSmsMessage("msg2", "sender2"),
                )
            },
        )
        val intent = Intent()
        val actual = subject.getMessagesFromIntent(intent)
        assertEquals(SmsIntentParserService.ParsedSmsMessage("sender1", "msg1msg2"), actual)
    }

    private fun mockSmsMessage(message: String, sender: String): SmsMessage {
        return mock {
            on { displayMessageBody }.thenReturn(message)
            on { originatingAddress }.thenReturn(sender)
        }
    }
}
