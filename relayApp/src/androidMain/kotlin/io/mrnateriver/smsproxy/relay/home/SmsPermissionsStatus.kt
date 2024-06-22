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

@Preview
@Composable
fun SmsPermissionsStatus(
    modifier: Modifier = Modifier,
    permissionState: PermissionState = PermissionState.UNKNOWN,
) {
    if (permissionState == PermissionState.UNKNOWN) {
        PermissionStatusCard {
            Row(
                modifier = Modifier.padding(AppSpacings.medium),
                horizontalArrangement = Arrangement.spacedBy(AppSpacings.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)

                Text(
                    text = "Requesting permissions...", // TODO: proper text + i18n
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    } else if (permissionState == PermissionState.DENIED) {
        PermissionStatusCard(error = true) {
            Column(
                modifier = Modifier.padding(AppSpacings.medium),
                verticalArrangement = Arrangement.spacedBy(AppSpacings.small),
            ) {
                // TODO: i18n
                Text(text = "Permission denied", style = MaterialTheme.typography.titleMedium)
                // TODO: proper text + i18n
                Text(text = "Please grant the necessary permissions for the app to function.")
            }
        }
    }
}

@Preview
@Composable
private fun PermissionStatusCard(
    modifier: Modifier = Modifier,
    error: Boolean = false,
    content: @Composable () -> Unit = {},
) {

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = if (error) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
        content = content,
    )
}