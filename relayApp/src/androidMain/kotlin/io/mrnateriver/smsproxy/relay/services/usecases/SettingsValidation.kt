package io.mrnateriver.smsproxy.relay.services.usecases

import java.net.MalformedURLException
import java.net.URL

enum class ApiBaseUrlValidationResult {
    VALID,
    INVALID_EMPTY,
    INVALID_FORMAT,
}

fun validateBaseApiUrl(value: String): ApiBaseUrlValidationResult {
    return if (value.isEmpty()) {
        ApiBaseUrlValidationResult.INVALID_EMPTY
    } else {
        try {
            URL(value)
            ApiBaseUrlValidationResult.VALID
        } catch (e: MalformedURLException) {
            ApiBaseUrlValidationResult.INVALID_FORMAT
        }
    }
}

enum class ReceiverKeyValidationResult {
    VALID,
    INVALID_FORMAT,
}

fun validateReceiverKey(value: String): ReceiverKeyValidationResult {
    return value.matches(RECEIVER_KEY_REGEX)
        .let { if (!it) ReceiverKeyValidationResult.INVALID_FORMAT else ReceiverKeyValidationResult.VALID }
}

private val RECEIVER_KEY_REGEX = Regex("""^[a-zA-Z0-9]{16}$""")
