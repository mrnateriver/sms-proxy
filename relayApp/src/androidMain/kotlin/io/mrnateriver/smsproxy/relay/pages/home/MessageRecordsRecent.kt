package io.mrnateriver.smsproxy.relay.pages.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import io.mrnateriver.smsproxy.shared.theme.AppSpacings
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.UUID

@Composable
fun MessageRecordsRecent(
    modifier: Modifier = Modifier,
    entries: List<MessageEntry> = listOf(),
) {
    if (entries.isEmpty()) {
        return
    }

    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM) }
    val timeZone = remember { TimeZone.currentSystemDefault() }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AppSpacings.medium)) {
        Text(
            modifier = Modifier.padding(start = AppSpacings.medium),
            text = stringResource(R.string.dashboard_recent_messages_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        entries.forEach { entry ->
            // TODO: show a processing status indicator based on entry.sendStatus
            MessageRecord(
                from = entry.messageData.sender,
                message = entry.messageData.message,
                timestamp = dateFormatter.format(
                    entry.messageData.receivedAt.toLocalDateTime(timeZone)
                        .toJavaLocalDateTime()
                ),
            )
        }
    }
}

private const val MESSAGE_RECORD_TEXT_MAX_LINES = 5

@Composable
private fun MessageRecord(
    modifier: Modifier = Modifier,
    from: String,
    message: String,
    timestamp: String,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        Column(
            modifier = Modifier
                .padding(AppSpacings.medium)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppSpacings.small),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = from,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = message,
                maxLines = MESSAGE_RECORD_TEXT_MAX_LINES,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun MessageRecordsRecentPreview() {
    MessageRecordsRecent(entries = previewMessageRecords)
}

@Preview
@Composable
private fun MessageRecordPreview(
) {
    MessageRecord(
        modifier = Modifier,
        from = "+12223334455",
        message = "Hello World",
        timestamp = "24.06.2024 15:36:23 UTC+2",
    )
}

val previewMessageRecords = listOf(
    MessageEntry(
        guid = UUID.randomUUID(),
        externalId = null,
        sendRetries = 0,
        sendFailureReason = null,
        sendStatus = MessageRelayStatus.SUCCESS,
        createdAt = null,
        updatedAt = null,
        messageData = MessageData(
            sender = "+12223334455",
            message = "Hello World",
            receivedAt = Clock.System.now(),
        )
    ),
    MessageEntry(
        guid = UUID.randomUUID(),
        externalId = null,
        sendRetries = 0,
        sendFailureReason = null,
        sendStatus = MessageRelayStatus.SUCCESS,
        createdAt = null,
        updatedAt = null,
        messageData = MessageData(
            sender = "Hello",
            message = "General Kenobi",
            receivedAt = Clock.System.now(),
        )
    ),
    MessageEntry(
        guid = UUID.randomUUID(),
        externalId = null,
        sendRetries = 0,
        sendFailureReason = null,
        sendStatus = MessageRelayStatus.SUCCESS,
        createdAt = null,
        updatedAt = null,
        messageData = MessageData(
            sender = "+993742732",
            message = "Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. Test a very-very long text that should be truncated. ",
            receivedAt = Clock.System.now(),
        )
    ),
    MessageEntry(
        guid = UUID.randomUUID(),
        externalId = null,
        sendRetries = 0,
        sendFailureReason = null,
        sendStatus = MessageRelayStatus.SUCCESS,
        createdAt = null,
        updatedAt = null,
        messageData = MessageData(
            sender = "World",
            message = "How's it going",
            receivedAt = Clock.System.now(),
        )
    ),
)
