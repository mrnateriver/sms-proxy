package io.mrnateriver.smsproxy.relay.services.settings

import io.mrnateriver.smsproxy.relay.services.usecases.ApiBaseUrlValidationResult
import org.junit.Assert.assertEquals
import org.junit.Test

class ApiBaseUrlSettingTest {
    @Test
    fun validateBaseApiUrl_validUrl_shouldReturnEmptyString() {
        val url = "http://example.com"
        val result = io.mrnateriver.smsproxy.relay.services.usecases.validateBaseApiUrl(url)
        assertEquals(ApiBaseUrlValidationResult.VALID, result)
    }

    @Test
    fun validateBaseApiUrl_emptyUrl_shouldReturnError() {
        val url = ""
        val result = io.mrnateriver.smsproxy.relay.services.usecases.validateBaseApiUrl(url)
        assertEquals(ApiBaseUrlValidationResult.INVALID_EMPTY, result)
    }

    @Test
    fun validateBaseApiUrl_invalidUrl_shouldReturnError() {
        val url = "hey:whatever"
        val result = io.mrnateriver.smsproxy.relay.services.usecases.validateBaseApiUrl(url)
        assertEquals(ApiBaseUrlValidationResult.INVALID_FORMAT, result)
    }
}