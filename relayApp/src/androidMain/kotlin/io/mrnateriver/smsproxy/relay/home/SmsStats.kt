package io.mrnateriver.smsproxy.relay.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.shared.AppSpacings

@Preview
@Composable
fun SmsStats(modifier: Modifier = Modifier) {
    // TODO: data
    LazyVerticalGrid(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppSpacings.medium),
        horizontalArrangement = Arrangement.spacedBy(
            AppSpacings.medium
        ),
        columns = GridCells.Fixed(2)
    ) {
        item {
            StatsCard(title = "Received", value = "123")
        }

        item {
            StatsCard(title = "Relayed", value = "456")
        }

        item {
            StatsCard(title = "Errors", value = "42", textColor = MaterialTheme.colorScheme.error)
        }

        item {
            StatsCard(title = "Failures", value = "0", textColor = MaterialTheme.colorScheme.error)
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
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        modifier = modifier.aspectRatio(1.0f),
        color = MaterialTheme.colorScheme.tertiaryContainer
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
                color = textColor
            )
            Spacer(Modifier.weight(1f))
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = textColor)
            Text(
                text = lastEvent,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}