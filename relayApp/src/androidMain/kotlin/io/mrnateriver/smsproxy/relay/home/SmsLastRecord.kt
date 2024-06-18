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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.shared.AppSpacings

@Preview
@Composable
fun SmsLastRecord(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AppSpacings.medium)) {
        Text(text = "Last relayed SMS", style = MaterialTheme.typography.bodyLarge)
        SmsRecord(modifier = Modifier.fillMaxWidth())
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
        color = MaterialTheme.colorScheme.surfaceDim
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
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(text = message, style = MaterialTheme.typography.bodySmall)
        }
    }
}
