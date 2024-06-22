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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.shared.AppSpacings

@Stable
data class SmsStatsData(
    val received: Int = 0,
    val relayed: Int = 0,
    val errors: Int = 0,
    val failures: Int = 0,
)

@Preview
@Composable
fun SmsStats(modifier: Modifier = Modifier, data: SmsStatsData = SmsStatsData(123, 456, 42, 0)) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AppSpacings.medium)
    ) {
        Text(
            modifier = Modifier.padding(start = AppSpacings.medium),
            text = "Statistics", // TODO: i18n
            style = MaterialTheme.typography.headlineMedium,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacings.medium),
        ) {
            StatsCard(
                modifier = Modifier.weight(1f),
                title = "Received", // TODO: i18n
                value = data.received.toString()
            )
            StatsCard(
                modifier = Modifier.weight(1f),
                title = "Relayed",  // TODO: i18n
                value = data.relayed.toString()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacings.medium),
        ) {
            StatsCard(
                modifier = Modifier.weight(1f),
                title = "Errors", // TODO: i18n
                value = data.errors.toString(),
                textColor = MaterialTheme.colorScheme.error,
            )
            StatsCard(
                modifier = Modifier.weight(1f),
                title = "Failures", // TODO: i18n
                value = data.failures.toString(),
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