package io.mrnateriver.smsproxy.shared

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AlertMessageTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun alertMessage_shouldShowTitle() {
        rule.setContent { AlertMessage(title = "Test Title", text = "") }

        rule.onNodeWithText("Test Title").assertExists()
    }

    @Test
    fun alertMessage_shouldShowIcon() {
        rule.setContent {
            AlertMessage(
                text = "",
                textIconVector = Icons.Outlined.Settings,
                textIconContentDescription = "Test Icon"
            )
        }

        rule.onNodeWithContentDescription("Test Icon").assertExists()
    }

    @Test
    fun alertMessage_shouldShowText() {
        rule.setContent { AlertMessage(text = "Test Text") }

        rule.onNodeWithText("Test Text").assertExists()
    }

    @Test
    fun alertMessage_shouldCallProvidedCallbackOnActionButtonClick() {
        var called = false
        rule.setContent {
            AlertMessage(
                text = "",
                action = AlertMessageAction(label = "Test Action", action = { called = true })
            )
        }

        rule.onNodeWithText("Test Action").performClick()

        assertTrue(called)
    }
}