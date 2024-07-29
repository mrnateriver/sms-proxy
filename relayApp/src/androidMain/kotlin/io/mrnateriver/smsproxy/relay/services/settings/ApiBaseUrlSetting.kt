package io.mrnateriver.smsproxy.relay.services.settings

import android.content.res.Resources
import io.mrnateriver.smsproxy.relay.R
import java.net.MalformedURLException
import java.net.URL

fun validateBaseApiUrl(value: String, context: Resources): String {
    return if (value.isEmpty()) {
        context.getString(R.string.settings_page_entry_api_base_url_error_empty)
    } else {
        try {
            URL(value)
            return ""
        } catch (e: MalformedURLException) {
            return context.getString(R.string.settings_page_entry_api_base_url_error_invalid_format)
        }
    }
}
