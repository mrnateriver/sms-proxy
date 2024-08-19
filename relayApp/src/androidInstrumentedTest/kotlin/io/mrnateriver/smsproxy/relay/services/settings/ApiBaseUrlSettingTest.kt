package io.mrnateriver.smsproxy.relay.services.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import io.mrnateriver.smsproxy.relay.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ApiBaseUrlSettingTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun validateBaseApiUrl_validUrl_shouldReturnEmptyString() {
        val url = "http://example.com"
        val result = validateBaseApiUrl(url, rule.activity.resources)
        assertEquals(null, result)
    }

    @Test
    fun validateBaseApiUrl_emptyUrl_shouldReturnError() {
        val url = ""
        val result = validateBaseApiUrl(url, rule.activity.resources)
        assertEquals(
            rule.activity.getString(R.string.settings_page_entry_api_base_url_error_empty),
            result
        )
    }

    @Test
    fun validateBaseApiUrl_invalidUrl_shouldReturnError() {
        val url = "hey:whatever"
        val result = validateBaseApiUrl(url, rule.activity.resources)
        assertEquals(
            rule.activity.getString(R.string.settings_page_entry_api_base_url_error_invalid_format),
            result
        )
    }
}