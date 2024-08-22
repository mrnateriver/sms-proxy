package io.mrnateriver.smsproxy.relay.services.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class ReceiverKeySettingTest {
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