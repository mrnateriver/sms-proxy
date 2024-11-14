package io.mrnateriver.smsproxy.relay.layout.drawer

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.mrnateriver.smsproxy.relay.AppPages
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppDrawerContentsTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    // `Column`s are needed in these tests because `AppDrawerContents` renders multiple composables,
    // which would overlap without a `Column`, and that would make it impossible to "click" only one of them

    @Test
    fun appDrawerContents_shouldShowEntriesLabels() {
        rule.setContent {
            Column {
                AppDrawerContents()
            }
        }

        rule.onNodeWithText(rule.activity.getString(AppPages.SETTINGS.descriptor.titleResId))
            .assertExists()

        rule.onNodeWithText(rule.activity.getString(AppPages.ABOUT.descriptor.titleResId))
            .assertExists()
    }

    @Test
    fun appDrawerContents_shouldSetSelectedEntry() {
        var activePage by mutableStateOf(AppPages.SETTINGS)
        rule.setContent {
            Column {
                AppDrawerContents(activePage = activePage)
            }
        }

        rule.onNodeWithTag("entry-${AppPages.SETTINGS.name}")
            .assertIsSelected()

        activePage = AppPages.ABOUT

        rule.onNodeWithTag("entry-${AppPages.ABOUT.name}")
            .assertIsSelected()
    }

    @Test
    fun appDrawerContents_selectedEntryClicked_shouldToggleDrawer() {
        var toggled = false
        var navigated = false
        rule.setContent {
            Column {
                AppDrawerContents(
                    activePage = AppPages.SETTINGS,
                    toggleDrawer = { toggled = true },
                    onNavigateClick = { navigated = true },
                )
            }
        }

        rule.onNodeWithTag("entry-${AppPages.SETTINGS.name}").performClick()

        assertTrue(toggled)
        assertFalse(navigated)
    }

    @Test
    fun appDrawerContents_unselectedEntryClicked_shouldNavigate() {
        var toggled = false
        var navigatedWith: AppPages? = null
        rule.setContent {
            Column {
                AppDrawerContents(
                    activePage = AppPages.SETTINGS,
                    toggleDrawer = { toggled = true },
                    onNavigateClick = { navigatedWith = it },
                )
            }
        }

        rule.onNodeWithTag("entry-${AppPages.ABOUT.name}").performClick()

        assertFalse(toggled)
        assertTrue(navigatedWith === AppPages.ABOUT)
    }
}
