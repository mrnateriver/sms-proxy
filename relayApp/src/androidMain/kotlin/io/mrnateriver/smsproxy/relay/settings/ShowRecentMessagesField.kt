package io.mrnateriver.smsproxy.relay.settings

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import io.mrnateriver.smsproxy.relay.R
import me.zhanghai.compose.preference.checkboxPreference

private const val PREF_KEY_SHOW_RECENT_MESSAGES = "show-recent-messages"

fun LazyListScope.showRecentMessagesPreference() {
    checkboxPreference(
        key = PREF_KEY_SHOW_RECENT_MESSAGES,
        defaultValue = true,
        title = { Text(stringResource(R.string.settings_page_entry_recent_messages_title)) },
        summary = { Text(stringResource(R.string.settings_page_entry_recent_messages_summary)) },
    )
}
