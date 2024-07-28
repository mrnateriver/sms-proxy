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
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.theme.AppSpacings
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Preview
@Composable
fun SmsLastRecords(modifier: Modifier = Modifier, records: List<MessageData> = previewSmsRecords) {
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AppSpacings.medium)) {
        Text(
            modifier = Modifier.padding(start = AppSpacings.medium),
            text = stringResource(R.string.dashboard_last_messages_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        records.forEach { record ->
            SmsRecord(
                from = record.sender,
                message = record.message,
                timestamp = dateFormatter.format(
                    record.receivedAt.toLocalDateTime(TimeZone.currentSystemDefault())
                        .toJavaLocalDateTime()
                ),
            )
        }
    }
}

@Preview
@Composable
private fun SmsRecord(
    modifier: Modifier = Modifier,
    from: String = "+12223334455",
    message: String = "Hello World",
    timestamp: String = "24.06.2024 15:36:23 UTC+2",
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

            Text(text = message)
        }
    }
}

private val previewSmsRecords = listOf(
    MessageData(
        sender = "+12223334455",
        message = "Hello World",
        receivedAt = Clock.System.now(),
    ),
    MessageData(
        sender = "Hello",
        message = "General Kenobi",
        receivedAt = Clock.System.now(),
    ),
    MessageData(
        sender = "+993742732",
        message = "Test",
        receivedAt = Clock.System.now(),
    ),
    MessageData(
        sender = "World",
        message = "How's it going",
        receivedAt = Clock.System.now(),
    ),
)
