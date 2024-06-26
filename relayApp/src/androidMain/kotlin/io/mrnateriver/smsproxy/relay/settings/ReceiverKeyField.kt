package io.mrnateriver.smsproxy.relay.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Modifier
import me.zhanghai.compose.preference.rememberPreferenceState

const val PREF_KEY_API_SERVER_RECEIVER_KEY = "api-server-receiver-key"

fun LazyListScope.receiverKeyPreference() {
    item(key = PREF_KEY_API_SERVER_RECEIVER_KEY, contentType = "ValidatedStringFieldPreference") {
        val state = rememberPreferenceState(PREF_KEY_API_SERVER_RECEIVER_KEY, "")
        Box(modifier = Modifier.fillMaxWidth()) {
            ValidatedStringFieldPreference(
                state = state,
                title = "Receiver Key", // TODO: i18n
                validate = ::validateReceiverKey,
            )
        }

        HorizontalDivider()
    }
}

private val RECEIVER_KEY_REGEX = Regex("""^[a-zA-Z0-9]{16}$""")
private fun validateReceiverKey(value: String): String? {
    return value.matches(RECEIVER_KEY_REGEX)
        .let { if (!it) "Invalid receiver key specified" else null } // TODO: i18n
}

