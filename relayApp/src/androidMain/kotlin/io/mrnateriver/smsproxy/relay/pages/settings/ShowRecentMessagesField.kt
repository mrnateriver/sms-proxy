package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.composables.rememberMutableCoroutineState
import io.mrnateriver.smsproxy.relay.services.settings.SettingsService
import me.zhanghai.compose.preference.CheckboxPreference

fun LazyListScope.showRecentMessagesPreference(settingsService: SettingsService) {
    item(key = "show-recent-messages", contentType = "CheckboxPreference") {
        val state = rememberMutableCoroutineState(
            settingsService.showRecentMessages,
            settingsService::setShowRecentMessages,
            false,
        )

        CheckboxPreference(
            state = state,
            title = { Text(stringResource(R.string.settings_page_entry_recent_messages_title)) },
            summary = { Text(stringResource(R.string.settings_page_entry_recent_messages_summary)) },
        )
    }
}
