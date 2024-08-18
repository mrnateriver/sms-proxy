package io.mrnateriver.smsproxy.relay.layout

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.layout.drawer.LocalAppDrawerState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppLayoutTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun appLayout_shouldRenderDrawerContent() {
        rule.setContent {
            AppLayout(drawerContent = { _ ->
                Text(text = "Drawer content")
            })
        }

        rule.onNodeWithText("Drawer content").assertExists()
    }

    @Test
    fun appLayout_shouldRenderContent() {
        rule.setContent {
            AppLayout {
                Text(text = "Content")
            }
        }

        rule.onNodeWithText("Content").assertExists()
    }

    @Test
    fun appLayout_shouldShowTitle() {
        rule.setContent {
            AppLayout(title = "Test Title")
        }

        rule.onNodeWithText("Test Title").assertExists()
    }

    @Test
    fun appLayout_onHomePage_shouldShowDrawerToggleButton() {
        rule.setContent {
            AppLayout(isHomePage = true)
        }

        rule.onNodeWithContentDescription(rule.activity.getString(R.string.app_bar_navigation_button_label_drawer))
            .assertExists()
    }

    @Test
    fun appLayout_onHomePage_shouldToggleDrawerWhenButtonClicked() {
        rule.setContent {
            AppLayout(isHomePage = true) {
                val nestedDrawerState = LocalAppDrawerState.current
                Text("Drawer is ${if (nestedDrawerState.isOpen) "open" else "closed"}")
            }
        }

        rule.onNodeWithContentDescription(rule.activity.getString(R.string.app_bar_navigation_button_label_drawer))
            .performClick()

        rule.waitForIdle()

        rule.onNodeWithText("Drawer is open").assertExists()
    }

    @Test
    fun appLayout_onInnerPage_shouldShowBackButton() {
        rule.setContent {
            AppLayout(isHomePage = false)
        }

        rule.onNodeWithContentDescription(rule.activity.getString(R.string.app_bar_navigation_button_label_back))
            .assertExists()
    }

    @Test
    fun appLayout_onInnerPage_shouldNavigateUpWhenButtonClicked() {
        var navigateUpClicked = false
        rule.setContent {
            AppLayout(isHomePage = false, onNavigateUpClicked = { navigateUpClicked = true })
        }

        rule.onNodeWithContentDescription(rule.activity.getString(R.string.app_bar_navigation_button_label_back))
            .performClick()

        assertTrue(navigateUpClicked)
    }

}