package io.mrnateriver.smsproxy.relay.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.mrnateriver.smsproxy.shared.AppSpacings

@Composable
fun ServerSettingsStatus(modifier: Modifier = Modifier, onGoToSettingsClick: () -> Unit = {}) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier.padding(AppSpacings.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacings.medium),
        ) {
            // TODO: i18n
            Text(text = "Configure Server", style = MaterialTheme.typography.titleMedium)
            // TODO: proper text + i18n
            Text(text = "Configure the server settings to connect to the SMS Proxy server.")
            Button(onClick = onGoToSettingsClick) {
                // TODO: i18n
                Text(text = "Go to Settings")
            }
        }
    }
}