package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.shared.composables.AppPreferencesProvider
import me.zhanghai.compose.preference.CheckboxPreference

fun LazyListScope.showRecentMessagesPreference(state: MutableState<Boolean>) {
    item(key = "show-recent-messages", contentType = "CheckboxPreference") {
        ShowRecentMessagesPreference(state)
    }
}

@Composable
private fun ShowRecentMessagesPreference(state: MutableState<Boolean>) {
    CheckboxPreference(
        state = state,
        title = { Text(stringResource(R.string.settings_page_entry_recent_messages_title)) },
        summary = { Text(stringResource(R.string.settings_page_entry_recent_messages_summary)) },
    )
}

@Preview
@Composable
private fun ShowRecentMessagesPreferencePreview_Checked() {
    AppPreferencesProvider {
        ShowRecentMessagesPreference(remember { mutableStateOf(true) })
    }
}

@Preview
@Composable
private fun ShowRecentMessagesPreferencePreview_Unchecked() {
    AppPreferencesProvider {
        ShowRecentMessagesPreference(remember { mutableStateOf(false) })
    }
}
