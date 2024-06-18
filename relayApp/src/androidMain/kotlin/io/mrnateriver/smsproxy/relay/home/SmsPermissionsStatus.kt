package io.mrnateriver.smsproxy.relay.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
    permissionState: PermissionState = PermissionState.GRANTED
) {
    // TODO
    Column(modifier, verticalArrangement = Arrangement.spacedBy(AppSpacings.medium)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Row(
                modifier = Modifier.padding(AppSpacings.medium),
                horizontalArrangement = Arrangement.spacedBy(AppSpacings.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PermissionStateIcon(permissionState = permissionState)

                // TODO: text
                Text(text = "Permission: ${permissionState.name}")

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

@Preview
@Composable
fun PermissionStateIcon(
    permissionState: PermissionState = PermissionState.GRANTED
) {
    if (permissionState == PermissionState.UNKNOWN) {
        return CircularProgressIndicator()
    }

    val icon = when (permissionState) {
        PermissionState.GRANTED -> Icons.Filled.Done
        else -> Icons.Filled.Clear
    }

    val tint = when (permissionState) {
        PermissionState.GRANTED -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }

    Icon(
        imageVector = icon,
        tint = tint,
        contentDescription = "" // TODO: add content description
    )
}
