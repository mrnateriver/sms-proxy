package io.mrnateriver.smsproxy.relay.services.settings

import android.content.res.Resources
import io.mrnateriver.smsproxy.relay.R

fun validateReceiverKey(value: String, context: Resources): String? {
    return value.matches(RECEIVER_KEY_REGEX)
        .let { if (!it) context.getString(R.string.settings_page_entry_receiver_key_error_format) else null }
}

private val RECEIVER_KEY_REGEX = Regex("""^[a-zA-Z0-9]{16}$""")
