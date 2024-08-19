package io.mrnateriver.smsproxy.relay.services.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import io.mrnateriver.smsproxy.relay.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ReceiverKeySettingTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun validateReceiverKey_validKey_shouldReturnEmptyString() {
        val url = "test1234test1234"
        val result = validateReceiverKey(url, rule.activity.resources)
        assertEquals(null, result)
    }

    @Test
    fun validateReceiverKey_emptyKey_shouldReturnError() {
        val url = ""
        val result = validateReceiverKey(url, rule.activity.resources)
        assertEquals(
            rule.activity.getString(R.string.settings_page_entry_receiver_key_error_format),
            result
        )
    }

    @Test
    fun validateReceiverKey_invalidKey_shouldReturnError() {
        val url = "test1234test"
        val result = validateReceiverKey(url, rule.activity.resources)
        assertEquals(
            rule.activity.getString(R.string.settings_page_entry_receiver_key_error_format),
            result
        )
    }
}