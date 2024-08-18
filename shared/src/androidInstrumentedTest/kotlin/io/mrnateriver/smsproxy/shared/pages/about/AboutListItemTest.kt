package io.mrnateriver.smsproxy.shared.pages.about

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AboutListItemTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun aboutListItem_shouldShowProvidedTitleAndText() {
        rule.setContent {
            AboutListItem(title = "Test Title", text = "Test Text")
        }

        rule.onNodeWithText("Test Title").assertExists()
        rule.onNodeWithText("Test Text").assertExists()
    }

    @Test
    fun aboutListItem_shouldCallProvidedOnClickCallback() {
        var clicked = false
        rule.setContent {
            AboutListItem(title = "Test Title", text = "Test Text", onClick = { clicked = true })
        }

        rule.onRoot().performClick()

        assertTrue(clicked)
    }

    @Test
    fun aboutListItem_shouldShowArrowIconIfClickable() {
        rule.setContent {
            AboutListItem(
                title = "Test Title",
                text = "Test Text",
                trailingContentIconContentDescription = "Test Arrow Icon",
                onClick = {},
            )
        }

        rule.onNodeWithContentDescription("Test Arrow Icon").assertExists()
    }
}