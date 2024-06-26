package io.mrnateriver.smsproxy.relay.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Modifier
import me.zhanghai.compose.preference.rememberPreferenceState

const val PREF_KEY_API_SERVER_ADDRESS = "api-server-address"

fun LazyListScope.serverAddressPreference() {
    item(key = PREF_KEY_API_SERVER_ADDRESS, contentType = "ValidatedStringFieldPreference") {
        val state = rememberPreferenceState(PREF_KEY_API_SERVER_ADDRESS, "")
        Box(modifier = Modifier.fillMaxWidth()) {
            ValidatedStringFieldPreference(
                state = state,
                title = "Server Address", // TODO: i18n
                validate = ::validateServerAddress,
            )
        }

        HorizontalDivider()
    }
}

// TODO: I18N:

private fun validateServerAddress(value: String): String {
    return (if (value.isEmpty()) {
        "Server address must not be empty"
    } else if (value.resemblesIPv4()) {
        value.validateAsIPv4()
    } else null)
        ?: value.validateAsFQDN() ?: "Invalid FQDN or IPv4 address"
}

private val NAIVE_IPV4_REGEX = Regex("""^(\d+\.){3}\d+(:\d+)?$""")
private fun String.resemblesIPv4(): Boolean {
    return matches(NAIVE_IPV4_REGEX)
}

private fun String.validateAsIPv4(): String {
    try {
        val splitByPort = split(":")
        val ip = splitByPort[0]
        val port = splitByPort.getOrNull(1) ?: ""

        if (port.isNotEmpty()) {
            try {
                val portNumber = port.toInt()
                if (portNumber !in 1..65535) return "Invalid port number range"
            } catch (e: NumberFormatException) {
                return "Invalid port number"
            }
        }

        val parts = ip.split(".")
        if (parts.size == 4 && parts.all { it.isNotEmpty() && it.toInt() in 0..255 }) {
            return ""
        }
    } catch (e: Exception) {
        //
    }

    return "Invalid IPv4 address"
}

private val SERVER_ADDRESS_REGEX =
    Regex("""^(?=.{1,255}$)[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?)*(:\d+)?$""")

private fun String.validateAsFQDN(): String? {
    val matchResults = SERVER_ADDRESS_REGEX.find(this)
    return matchResults?.let {
        val port = it.groupValues[1]
        if (port.isNotEmpty()) {
            return try {
                if (port.substring(1).toInt() !in 1..65535) {
                    "Invalid port number range"
                } else ""
            } catch (e: NumberFormatException) {
                "Invalid port number"
            }
        }
        ""
    }
}
