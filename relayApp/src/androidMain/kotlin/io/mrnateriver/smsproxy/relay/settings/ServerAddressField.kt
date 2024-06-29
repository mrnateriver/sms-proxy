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

const val PREF_KEY_API_SERVER_ADDRESS = "api-server-address"

fun LazyListScope.serverAddressPreference() {
    item(key = PREF_KEY_API_SERVER_ADDRESS, contentType = "ValidatedStringFieldPreference") {
        val state = rememberPreferenceState(PREF_KEY_API_SERVER_ADDRESS, "")
        Box(modifier = Modifier.fillMaxWidth()) {
            val resources = LocalContext.current.resources
            ValidatedStringFieldPreference(
                state = state,
                title = stringResource(R.string.settings_page_entry_server_address_title),
                validate = { value -> validateServerAddress(value, resources) },
            )
        }

        HorizontalDivider()
    }
}

private fun validateServerAddress(value: String, context: Resources): String {
    return (if (value.isEmpty()) {
        context.getString(R.string.settings_page_entry_server_address_error_empty)
    } else if (value.resemblesIPv4()) {
        value.validateAsIPv4(context)
    } else null)
        ?: value.validateAsFQDN(context)
        ?: context.getString(R.string.settings_page_entry_server_address_error_invalid_format)
}

private val NAIVE_IPV4_REGEX = Regex("""^(\d+\.){3}\d+(:\d+)?$""")
private fun String.resemblesIPv4(): Boolean {
    return matches(NAIVE_IPV4_REGEX)
}

private fun String.validateAsIPv4(context: Resources): String {
    try {
        val splitByPort = split(":")
        val ip = splitByPort[0]
        val port = splitByPort.getOrNull(1) ?: ""

        if (port.isNotEmpty()) {
            try {
                val portNumber = port.toInt()
                if (portNumber !in 1..65535) {
                    return context.getString(R.string.settings_page_entry_server_address_error_port_range)
                }
            } catch (e: NumberFormatException) {
                return context.getString(R.string.settings_page_entry_server_address_error_port_format)
            }
        }

        val parts = ip.split(".")
        if (parts.size == 4 && parts.all { it.isNotEmpty() && it.toInt() in 0..255 }) {
            return ""
        }
    } catch (e: Exception) {
        //
    }

    return context.getString(R.string.settings_page_entry_server_address_error_invalid_ipv4)
}

private val SERVER_ADDRESS_REGEX =
    Regex("""^(?=.{1,255}$)[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?)*(:\d+)?$""")

private fun String.validateAsFQDN(context: Resources): String? {
    val matchResults = SERVER_ADDRESS_REGEX.find(this)
    return matchResults?.let {
        val port = it.groupValues[1]
        if (port.isNotEmpty()) {
            return try {
                if (port.substring(1).toInt() !in 1..65535) {
                    context.getString(R.string.settings_page_entry_server_address_error_port_range)
                } else ""
            } catch (e: NumberFormatException) {
                context.getString(R.string.settings_page_entry_server_address_error_port_format)
            }
        }
        ""
    }
}
