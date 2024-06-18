package io.mrnateriver.smsproxy.relay.home

import android.text.format.DateFormat
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.shared.AppSpacings
import io.mrnateriver.smsproxy.shared.SmsData
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@Preview
@Composable
fun SmsLastRecord(modifier: Modifier = Modifier) {
    val dateFormat = DateFormat.getMediumDateFormat(LocalContext.current)
    val dateFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)


    val records = remember {
        listOf(
            SmsData(
                sender = "+12223334455",
                message = "Hello World",
                receivedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ),
            SmsData(
                sender = "Hello",
                message = "General Kenobi",
                receivedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ),
            SmsData(
                sender = "+993742732",
                message = "Test",
                receivedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ),
            SmsData(
                sender = "World",
                message = "How's it going",
                receivedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            ),
        )
    }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AppSpacings.medium)) {
        Text(
            modifier = Modifier.padding(start = AppSpacings.medium),
            text = "Last messages", // i18n
            style = MaterialTheme.typography.headlineMedium,
        )
        records.forEach { record ->
            SmsRecord(
                from = record.sender,
                message = record.message,
                timestamp = dateFormatter.format(record.receivedAt.toJavaLocalDateTime())
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
    timestamp: String = "24.06.2024 15:36:23 UTC+2"
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(
            modifier = Modifier
                .padding(AppSpacings.medium)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppSpacings.small)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = from,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(text = message)
        }
    }
}
