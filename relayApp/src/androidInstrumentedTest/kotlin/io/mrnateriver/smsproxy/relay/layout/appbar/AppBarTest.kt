package io.mrnateriver.smsproxy.relay.layout.appbar

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.mrnateriver.smsproxy.relay.R
import org.junit.Rule
import org.junit.Test

class AppBarTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun title_shouldShowAppNameByDefault() {
        rule.setContent { AppBar() }

        rule.onNodeWithText(rule.activity.getString(R.string.app_name))
            .assertExists()
    }

    @Test
    fun title_shouldShowCustomTitle() {
        val customTitle = "Custom Title"
        rule.setContent { AppBar(title = customTitle) }

        rule.onNodeWithText(customTitle)
            .assertExists()
    }

    @Test
    fun navigationIcon_shouldShowMenuIconByDefault() {
        rule.setContent { AppBar(navigationIconContentDescription = "test-navigation-icon") }

        rule.onNodeWithContentDescription("test-navigation-icon")
            .assertExists()
    }

    @Test
    fun navigationIcon_shouldCallProvidedCallback() {
        var clicked = false
        rule.setContent {
            AppBar(
                navigationIconContentDescription = "test-navigation-icon",
                onNavigationButtonClick = { clicked = true },
            )
        }

        rule.onNodeWithContentDescription("test-navigation-icon")
            .performClick()

        assert(clicked)
    }
}
