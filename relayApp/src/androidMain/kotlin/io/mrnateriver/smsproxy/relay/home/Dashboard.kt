package io.mrnateriver.smsproxy.relay.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.permissions.PermissionState
import io.mrnateriver.smsproxy.shared.AppSpacings
import io.mrnateriver.smsproxy.shared.SmsData

@Preview
@Composable
fun Dashboard(
    modifier: Modifier = Modifier,
    smsPermissionsState: PermissionState = PermissionState.UNKNOWN,
    smsStatsData: SmsStatsData = SmsStatsData(),
    smsRecords: List<SmsData> = listOf(),
) {
    Column(
        modifier = modifier.padding(AppSpacings.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacings.medium),
    ) {
        SmsPermissionsStatus(permissionState = smsPermissionsState)
        SmsStats(data = smsStatsData)
        SmsLastRecords(records = smsRecords)
    }
}