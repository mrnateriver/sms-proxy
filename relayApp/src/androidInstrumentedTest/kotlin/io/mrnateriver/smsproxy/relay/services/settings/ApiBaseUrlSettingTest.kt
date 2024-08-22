package io.mrnateriver.smsproxy.relay.services.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class ApiBaseUrlSettingTest {
    @Test
    fun validateBaseApiUrl_validUrl_shouldReturnEmptyString() {
        val url = "http://example.com"
        val result = validateBaseApiUrl(url)
        assertEquals(ApiBaseUrlValidationResult.VALID, result)
    }

    @Test
    fun validateBaseApiUrl_emptyUrl_shouldReturnError() {
        val url = ""
        val result = validateBaseApiUrl(url)
        assertEquals(ApiBaseUrlValidationResult.INVALID_EMPTY, result)
    }

    @Test
    fun validateBaseApiUrl_invalidUrl_shouldReturnError() {
        val url = "hey:whatever"
        val result = validateBaseApiUrl(url)
        assertEquals(ApiBaseUrlValidationResult.INVALID_FORMAT, result)
    }
}