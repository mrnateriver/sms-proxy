package io.mrnateriver.smsproxy.relay.pages.home

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.composables.PermissionState
import io.mrnateriver.smsproxy.shared.theme.AppSpacings

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
                    text = stringResource(R.string.dashboard_permissions_status_text_pending),
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
    } else if (permissionState == PermissionState.DENIED) {
        // TODO: refactor into an "ErrorCard" or something
        PermissionStatusCard(error = true) {
            Column(
                modifier = Modifier.padding(AppSpacings.medium),
                verticalArrangement = Arrangement.spacedBy(AppSpacings.small),
            ) {
                Text(
                    text = stringResource(R.string.dashboard_permissions_status_denied_title),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(text = stringResource(R.string.dashboard_permissions_status_denied_text))
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