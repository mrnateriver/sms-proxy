package io.mrnateriver.smsproxy.relay.settings

import android.content.res.Resources
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.mrnateriver.smsproxy.relay.R
import me.zhanghai.compose.preference.rememberPreferenceState

const val PREF_KEY_API_SERVER_RECEIVER_KEY = "api-server-receiver-key"

fun LazyListScope.receiverKeyPreference() {
    item(key = PREF_KEY_API_SERVER_RECEIVER_KEY, contentType = "ValidatedStringFieldPreference") {
        val state = rememberPreferenceState(PREF_KEY_API_SERVER_RECEIVER_KEY, "")
        Box(modifier = Modifier.fillMaxWidth()) {
            val resources = LocalContext.current.resources
            ValidatedStringFieldPreference(
                state = state,
                title = stringResource(R.string.settings_page_entry_receiver_key_title),
                validate = { value -> validateReceiverKey(value, resources) },
            )
        }

        HorizontalDivider()
    }
}

private val RECEIVER_KEY_REGEX = Regex("""^[a-zA-Z0-9]{16}$""")
private fun validateReceiverKey(value: String, context: Resources): String? {
    return value.matches(RECEIVER_KEY_REGEX)
        .let { if (!it) context.getString(R.string.settings_page_entry_receiver_key_error_format) else null }
}

