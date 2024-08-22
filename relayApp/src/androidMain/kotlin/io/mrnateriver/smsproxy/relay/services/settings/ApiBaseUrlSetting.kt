package io.mrnateriver.smsproxy.relay.services.settings

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
            return ApiBaseUrlValidationResult.VALID
        } catch (e: MalformedURLException) {
            return ApiBaseUrlValidationResult.INVALID_FORMAT
        }
    }
}
