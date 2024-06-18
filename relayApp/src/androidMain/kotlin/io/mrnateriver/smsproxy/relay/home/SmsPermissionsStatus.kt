package io.mrnateriver.smsproxy.relay.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.mrnateriver.smsproxy.relay.permissions.PermissionState
import io.mrnateriver.smsproxy.shared.AppSpacings

/**
 * UI element that displays the current state of the SMS permissions, necessary for
 * the application to function.
 */
@Preview
@Composable
fun SmsPermissionsStatus(
    modifier: Modifier = Modifier,
    permissionState: PermissionState = PermissionState.UNKNOWN
) {
    // TODO: remove column once proper state is implemented
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppSpacings.medium)) {
        if (true || permissionState == PermissionState.UNKNOWN) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Row(
                    modifier = Modifier.padding(AppSpacings.medium),
                    horizontalArrangement = Arrangement.spacedBy(AppSpacings.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)

                    // TODO: text
                    Text(text = "Requesting permissions...", style = MaterialTheme.typography.titleSmall)
                }
            }
        }

        // TODO: if denied
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.errorContainer
        ) {
            Column(
                modifier = Modifier.padding(AppSpacings.medium),
                verticalArrangement = Arrangement.spacedBy(AppSpacings.small)
            ) {
                Text(text = "Permission denied", style = MaterialTheme.typography.titleMedium)
                Text(text = "Please grant the necessary permissions for the app to function.")
            }
        }
    }
}
