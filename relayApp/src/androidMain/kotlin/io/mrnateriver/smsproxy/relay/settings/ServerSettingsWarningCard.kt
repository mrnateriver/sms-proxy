package io.mrnateriver.smsproxy.relay.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.mrnateriver.smsproxy.shared.AppSpacings

@Composable
fun ServerSettingsWarningCard(modifier: Modifier = Modifier) {
    // TODO: refactor into a reusable card along with ServerSettingsStatus
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(AppSpacings.medium),
            horizontalArrangement = Arrangement.spacedBy(AppSpacings.medium),
        ) {
            Icon(imageVector = Icons.Outlined.Info, contentDescription = null)
            // TODO: proper text + i18n
            Text(text = "Server address and receiver key must be set for proxying messages.")
        }
    }
}