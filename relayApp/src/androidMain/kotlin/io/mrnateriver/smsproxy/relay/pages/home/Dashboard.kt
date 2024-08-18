package io.mrnateriver.smsproxy.relay.pages.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.composables.PermissionStatus
import io.mrnateriver.smsproxy.relay.services.MessageStatsData
import io.mrnateriver.smsproxy.shared.models.MessageEntry
import io.mrnateriver.smsproxy.shared.theme.AppSpacings

@Composable
fun Dashboard(
    modifier: Modifier = Modifier,
    onGoToSettingsClick: () -> Unit = {},
    showApiKeyError: Boolean = true,
    showApiSettingsHint: Boolean = true,
    showMissingApiCertificatesError: Boolean = true,
    messagePermissionStatus: PermissionStatus = PermissionStatus.UNKNOWN,
    messageStatsData: MessageStatsData = MessageStatsData(),
    messageRecordsRecent: List<MessageEntry> = listOf(),
) {
    Column(
        modifier = modifier.padding(AppSpacings.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacings.medium),
    ) {
        if (showApiKeyError) {
            ApiKeyStatus(modifier = Modifier.semantics { testTag = "card-error-api-key" })
        }
        if (showApiSettingsHint) {
            ApiSettingsStatus(
                modifier = Modifier.semantics { testTag = "card-settings-hint" },
                onGoToSettingsClick = onGoToSettingsClick,
            )
        }
        if (showMissingApiCertificatesError) {
            ApiCertificatesStatus(
                modifier = Modifier.semantics { testTag = "card-error-certificates" },
            )
        }
        MessagePermissionsStatus(status = messagePermissionStatus)
        MessageStats(data = messageStatsData)
        MessageRecordsRecent(entries = messageRecordsRecent)
    }
}

@Preview
@Composable
private fun DashboardPreview_Minimal() {
    Dashboard(
        showApiKeyError = false,
        showApiSettingsHint = false,
        showMissingApiCertificatesError = false,
        messagePermissionStatus = PermissionStatus.GRANTED,
        messageStatsData = MessageStatsData(),
        messageRecordsRecent = listOf(),
    )
}

@Preview(heightDp = 1240)
@Composable
private fun DashboardPreview_Full() {
    Dashboard(
        showApiKeyError = true,
        showApiSettingsHint = true,
        showMissingApiCertificatesError = true,
        messagePermissionStatus = PermissionStatus.DENIED,
        messageStatsData = previewMessageStatsData,
        messageRecordsRecent = previewMessageRecords,
    )
}
