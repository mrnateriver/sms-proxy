package io.mrnateriver.smsproxy.relay.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.shared.AppSpacings
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Stable
data class SmsStatsData(
    val received: Int = 0,
    val relayed: Int = 0,
    val errors: Int = 0,
    val failures: Int = 0,
    val lastReceivedAt: LocalDateTime? = null,
    val lastRelayedAt: LocalDateTime? = null,
    val lastErrorAt: LocalDateTime? = null,
    val lastFailureAt: LocalDateTime? = null,
)

@Preview
@Composable
fun SmsStats(
    modifier: Modifier = Modifier,
    // TODO: move this to a preview definition
    data: SmsStatsData = SmsStatsData(
        123,
        456,
        42,
        0,
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        null
    ),
) {
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM) }

    fun formatDate(date: LocalDateTime?): String {
        return date?.let { dateFormatter.format(it.toJavaLocalDateTime()) } ?: ""
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacings.medium)
    ) {
        Text(
            modifier = Modifier.padding(start = AppSpacings.medium),
            text = stringResource(R.string.dashboard_stats_title),
            style = MaterialTheme.typography.headlineMedium,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacings.medium),
        ) {
            StatsCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.dashboard_stats_card_title_received),
                value = data.received.toString(),
                lastEvent = formatDate(data.lastReceivedAt),
            )
            StatsCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.dashboard_stats_card_title_relayed),
                value = data.relayed.toString(),
                lastEvent = formatDate(data.lastRelayedAt),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacings.medium),
        ) {
            StatsCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.dashboard_stats_card_title_errors),
                value = data.errors.toString(),
                lastEvent = formatDate(data.lastErrorAt),
                textColor = MaterialTheme.colorScheme.error,
            )
            StatsCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.dashboard_stats_card_title_failures),
                value = data.failures.toString(),
                lastEvent = formatDate(data.lastFailureAt),
                textColor = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview
@Composable
private fun StatsCard(
    modifier: Modifier = Modifier,
    title: String = "Sent",
    value: String = "123",
    lastEvent: String = "Last event: 1 minute ago",
    textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Column(
            modifier = Modifier.padding(AppSpacings.medium),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                color = textColor,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
            )
            Text(
                text = lastEvent,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}