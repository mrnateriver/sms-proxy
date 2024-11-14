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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsData
import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsEntry
import io.mrnateriver.smsproxy.shared.composables.theme.AppSpacings
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun MessageStats(data: MessageStatsData, modifier: Modifier = Modifier) {
    val dateFormatter = remember { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM) }

    fun formatDate(date: LocalDateTime?): String {
        return date?.let { dateFormatter.format(it.toJavaLocalDateTime()) } ?: ""
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacings.medium),
    ) {
        Text(
            modifier = Modifier.padding(start = AppSpacings.medium),
            text = stringResource(R.string.dashboard_stats_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacings.medium),
        ) {
            StatsCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.dashboard_stats_card_title_processed),
                value = data.processed.value.toString(),
                lastEvent = formatDate(data.processed.lastEvent),
            )
            StatsCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.dashboard_stats_card_title_relayed),
                value = data.relayed.value.toString(),
                lastEvent = formatDate(data.relayed.lastEvent),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacings.medium),
        ) {
            StatsCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.dashboard_stats_card_title_errors),
                value = data.errors.value.toString(),
                lastEvent = formatDate(data.errors.lastEvent),
                textColor = MaterialTheme.colorScheme.error,
            )
            StatsCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.dashboard_stats_card_title_failures),
                value = data.failures.value.toString(),
                lastEvent = formatDate(data.failures.lastEvent),
                textColor = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun StatsCard(
    title: String,
    value: String,
    lastEvent: String,
    modifier: Modifier = Modifier,
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
                modifier = Modifier.semantics { testTag = "entry-value" },
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
            )
            Text(
                text = lastEvent,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.semantics { testTag = "entry-date" },
            )
        }
    }
}

@Preview
@Composable
private fun MessageStatsPreview() {
    MessageStats(
        data = previewMessageStatsData,
    )
}

@Preview
@Composable
private fun MessageStatsPreview_NoData() {
    MessageStats(data = MessageStatsData())
}

@Preview
@Composable
private fun StatsCardPreview() {
    StatsCard(
        title = "Sent",
        value = "123",
        lastEvent = "Last event: 1 minute ago",
        textColor = MaterialTheme.colorScheme.onSurface,
    )
}

val previewMessageStatsData = MessageStatsData(
    processed = MessageStatsEntry(value = 123, Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())),
    relayed = MessageStatsEntry(value = 456, Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())),
    errors = MessageStatsEntry(value = 42, Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())),
    failures = MessageStatsEntry(value = 0, Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())),
)
