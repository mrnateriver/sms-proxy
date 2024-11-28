package io.mrnateriver.smsproxy.relay.pages.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import arrow.core.left
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.junit.Rule
import org.junit.Test
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.UUID

class MessageRecordsRecentTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val now = Instant.fromEpochMilliseconds(1723996071981)

    @Test
    fun messageRecordsRecent_shouldShowTitle() {
        rule.setContent {
            MessageRecordsRecent(entries = listOf(createMessageEntry("Alice", "Hello, Bob!")))
        }

        rule.onNodeWithText(rule.activity.getString(R.string.dashboard_recent_messages_title))
            .assertExists()
    }

    @Test
    fun messageRecordsRecent_shouldShowEntriesSenders() {
        rule.setContent {
            MessageRecordsRecent(entries = listOf(createMessageEntry("Alice", "Hello, Bob!")))
        }

        rule.onNodeWithText("Alice").assertExists()
    }

    @Test
    fun messageRecordsRecent_shouldShowEntriesCreateTimestamps() {
        rule.setContent {
            MessageRecordsRecent(entries = listOf(createMessageEntry("Alice", "Hello, Bob!")))
        }

        val timeZone = TimeZone.currentSystemDefault()
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)

        rule.onNodeWithText(formatter.format(now.toLocalDateTime(timeZone).toJavaLocalDateTime()))
            .assertExists()
    }

    @Test
    fun messageRecordsRecent_shouldShowEntriesMessages() {
        rule.setContent {
            MessageRecordsRecent(entries = listOf(createMessageEntry("Alice", "Hello, Bob!")))
        }

        rule.onNodeWithText("Hello, Bob!").assertExists()
    }

    @Test
    fun messageRecordsRecent_shouldShowEntriesStatus_Error() {
        rule.setContent {
            MessageRecordsRecent(
                entries = listOf(
                    createMessageEntry(
                        "Alice",
                        "Hello, Bob!",
                        MessageRelayStatus.ERROR,
                    ),
                ),
            )
        }

        rule.onNodeWithContentDescription(rule.activity.getString(R.string.dashboard_recent_messages_status_error))
            .assertExists()
    }

    @Test
    fun messageRecordsRecent_shouldShowEntriesStatus_Failure() {
        rule.setContent {
            MessageRecordsRecent(
                entries = listOf(
                    createMessageEntry(
                        "Alice",
                        "Hello, Bob!",
                        MessageRelayStatus.FAILED,
                    ),
                ),
            )
        }

        rule.onNodeWithContentDescription(rule.activity.getString(R.string.dashboard_recent_messages_status_error))
            .assertExists()
    }

    @Test
    fun messageRecordsRecent_shouldShowEntriesStatus_Success() {
        rule.setContent {
            MessageRecordsRecent(
                entries = listOf(
                    createMessageEntry(
                        "Alice",
                        "Hello, Bob!",
                        MessageRelayStatus.SUCCESS,
                    ),
                ),
            )
        }

        rule.onNodeWithContentDescription(rule.activity.getString(R.string.dashboard_recent_messages_status_success))
            .assertExists()
    }

    @Test
    fun messageRecordsRecent_shouldShowEntriesStatus_Progress() {
        rule.setContent {
            MessageRecordsRecent(
                entries = listOf(
                    createMessageEntry(
                        "Alice",
                        "Hello, Bob!",
                        MessageRelayStatus.IN_PROGRESS,
                    ),
                ),
            )
        }

        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertExists()
    }

    @Test
    fun messageRecordsRecent_shouldShowAllProvidedEntries() {
        rule.setContent {
            MessageRecordsRecent(
                entries = listOf(
                    createMessageEntry("sender 1", "msg 1"),
                    createMessageEntry("sender 2", "msg 2"),
                    createMessageEntry("sender 3", "msg 3"),
                    createMessageEntry("sender 4", "msg 4"),
                ),
            )
        }

        rule.onAllNodesWithText("sender ", true).assertCountEquals(4)
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
            ).left(),
            sendStatus = status,
            sendRetries = 1,
            sendFailureReason = null,
            updatedAt = now,
            createdAt = now,
        )
    }
}
