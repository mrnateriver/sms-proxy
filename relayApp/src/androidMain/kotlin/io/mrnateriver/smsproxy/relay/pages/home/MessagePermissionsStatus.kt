package io.mrnateriver.smsproxy.relay.pages.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.composables.PermissionStatus
import io.mrnateriver.smsproxy.shared.composables.AlertMessage
import io.mrnateriver.smsproxy.shared.composables.AlertMessageType
import io.mrnateriver.smsproxy.shared.composables.theme.AppSpacings

@Composable
fun MessagePermissionsStatus(
    modifier: Modifier = Modifier,
    status: PermissionStatus = PermissionStatus.UNKNOWN,
) {
    if (status == PermissionStatus.UNKNOWN) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
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
    } else if (status == PermissionStatus.DENIED) {
        AlertMessage(
            modifier = modifier,
            type = AlertMessageType.ERROR,
            text = stringResource(R.string.dashboard_permissions_status_denied_text),
            title = stringResource(R.string.dashboard_permissions_status_denied_title),
        )
    }
}

@Preview
@Composable
private fun MessagePermissionsStatusPreview_Unknown() {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(8.dp),
    ) {
        MessagePermissionsStatus(status = PermissionStatus.UNKNOWN)
    }
}

@Preview
@Composable
private fun MessagePermissionsStatusPreview_Denied() {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(8.dp),
    ) {
        MessagePermissionsStatus(status = PermissionStatus.DENIED)
    }
}

@Preview
@Composable
private fun MessagePermissionsStatusPreview_Granted() {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(8.dp),
    ) {
        MessagePermissionsStatus(status = PermissionStatus.GRANTED)
    }
}
