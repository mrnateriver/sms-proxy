package io.mrnateriver.smsproxy.relay.settings

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import me.zhanghai.compose.preference.checkboxPreference

private const val PREF_KEY_SHOW_RECENT_MESSAGES = "show-recent-messages"

fun LazyListScope.showRecentMessagesPreference() {
    checkboxPreference(
        key = PREF_KEY_SHOW_RECENT_MESSAGES,
        defaultValue = true,
        title = { Text("Show Recent Messages") }, // TODO: i18n
        summary = { Text("Show recent messages on the home screen") }, // TODO: i18n
    )
}
