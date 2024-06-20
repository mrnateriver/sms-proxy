package io.mrnateriver.smsproxy.relay.home.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.permissions.PermissionState
import io.mrnateriver.smsproxy.relay.permissions.rememberSmsPermissions
import io.mrnateriver.smsproxy.shared.AppSpacings

@Preview
@Composable
fun Dashboard(modifier: Modifier = Modifier) {
    val preview = LocalInspectionMode.current

    // FIXME: move to an initialization service or something
    val receiveSmsPermissionResult =
        if (preview) PermissionState.UNKNOWN else rememberSmsPermissions()

    Column(
        modifier = modifier.padding(AppSpacings.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacings.medium),
    ) {
        SmsPermissionsStatus(
            modifier = Modifier.fillMaxWidth(),
            permissionState = receiveSmsPermissionResult,
        )

        SmsStats()

        SmsLastRecord()
    }
}