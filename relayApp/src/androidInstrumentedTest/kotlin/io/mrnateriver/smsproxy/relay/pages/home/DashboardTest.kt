package io.mrnateriver.smsproxy.relay.pages.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class DashboardTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun dashboard_shouldShowApiKeyErrorIfSet() {
        rule.setContent {
            Dashboard(
                showApiKeyError = true,
                showApiSettingsHint = false,
                showMissingApiCertificatesError = false,
            )
        }

        rule.onNodeWithTag("card-error-api-key").assertExists()
    }

    @Test
    fun dashboard_shouldShowApiSettingsHintIfSet() {
        rule.setContent {
            Dashboard(
                showApiSettingsHint = true,
                showApiKeyError = false,
                showMissingApiCertificatesError = false,
            )
        }

        rule.onNodeWithTag("card-settings-hint").assertExists()
    }

    @Test
    fun dashboard_shouldShowApiCertificatesErrorIfSet() {
        rule.setContent {
            Dashboard(
                showMissingApiCertificatesError = true,
                showApiKeyError = false,
                showApiSettingsHint = false,
            )
        }

        rule.onNodeWithTag("card-error-certificates").assertExists()
    }
}