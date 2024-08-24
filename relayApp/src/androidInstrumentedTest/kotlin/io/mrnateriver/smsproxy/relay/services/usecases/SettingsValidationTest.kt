package io.mrnateriver.smsproxy.relay.services.usecases

import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsValidationTest {
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

    @Test
    fun validateReceiverKey_validKey_shouldReturnEmptyString() {
        val url = "test1234test1234"
        val result = validateReceiverKey(url)
        assertEquals(ReceiverKeyValidationResult.VALID, result)
    }

    @Test
    fun validateReceiverKey_emptyKey_shouldReturnError() {
        val url = ""
        val result = validateReceiverKey(url)
        assertEquals(ReceiverKeyValidationResult.INVALID_FORMAT, result)
    }

    @Test
    fun validateReceiverKey_invalidKey_shouldReturnError() {
        val url = "test1234test"
        val result = validateReceiverKey(url)
        assertEquals(ReceiverKeyValidationResult.INVALID_FORMAT, result)
    }
}
