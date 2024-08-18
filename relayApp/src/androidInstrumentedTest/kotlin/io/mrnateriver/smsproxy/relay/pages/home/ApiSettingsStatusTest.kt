package io.mrnateriver.smsproxy.relay.pages.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.mrnateriver.smsproxy.relay.R
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ApiSettingsStatusTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun apiSettingsStatus_shouldCallProvidedCallback() {
        var called = false
        rule.setContent {
            ApiSettingsStatus(
                onGoToSettingsClick = { called = true }
            )
        }

        rule.onNodeWithText(rule.activity.getString(R.string.home_page_api_settings_card_button_label))
            .performClick()

        assertTrue(called)
    }
}