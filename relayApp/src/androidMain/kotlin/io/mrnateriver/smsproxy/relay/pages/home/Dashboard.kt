package io.mrnateriver.smsproxy.relay.pages.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.composables.PermissionStatus
import io.mrnateriver.smsproxy.relay.services.MessageStatsData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.theme.AppSpacings

@Preview
@Composable
fun Dashboard(
    modifier: Modifier = Modifier,
    onGoToSettingsClick: () -> Unit = {},
    showApiKeyError: Boolean = true,
    showServerSettingsHint: Boolean = true,
    messagePermissionStatus: PermissionStatus = PermissionStatus.UNKNOWN,
    messageStatsData: MessageStatsData = MessageStatsData(),
    messageRecordsRecent: List<MessageEntry> = listOf(),
) {
    Column(
        modifier = modifier.padding(AppSpacings.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacings.medium),
    ) {
        if (showApiKeyError) {
            ApiKeyStatus()
        }
        if (showServerSettingsHint) {
            ServerSettingsStatus(onGoToSettingsClick = onGoToSettingsClick)
        }
        MessagePermissionsStatus(status = messagePermissionStatus)
        MessageStats(data = messageStatsData)
        MessageRecordsRecent(entries = messageRecordsRecent)
    }
}