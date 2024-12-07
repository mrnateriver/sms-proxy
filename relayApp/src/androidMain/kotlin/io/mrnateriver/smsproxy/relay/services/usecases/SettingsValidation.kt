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
    INVALID_EMPTY,
}

fun validateReceiverKey(value: String): ReceiverKeyValidationResult {
    return if (value.isEmpty()) {
        ReceiverKeyValidationResult.INVALID_EMPTY
    } else {
        ReceiverKeyValidationResult.VALID
    }
}
