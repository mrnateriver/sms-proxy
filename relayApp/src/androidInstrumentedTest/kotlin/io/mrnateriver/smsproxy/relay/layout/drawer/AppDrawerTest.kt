package io.mrnateriver.smsproxy.relay.layout.drawer

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppDrawerTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun appDrawer_shouldRenderContent() {
        rule.setContent {
            AppDrawer { _, _ -> Text("Hello, World!") }
        }

        rule.onNodeWithText("Hello, World!").assertExists()
    }

    @Test
    fun appDrawer_shouldRenderDrawerContent() {
        rule.setContent {
            AppDrawer(drawerContent = { _ -> Text("Drawer Content") })
        }

        rule.onNodeWithText("Drawer Content").assertExists()
    }

    @Test
    fun appDrawer_renderedContent_toggleDrawerCallback() {
        val drawerState = DrawerState(DrawerValue.Closed)

        rule.setContent {
            AppDrawer(drawerState = drawerState) { _, toggleDrawer ->
                Button(onClick = toggleDrawer) { Text(text = "Toggle Drawer") }
            }
        }

        rule.onNodeWithText("Toggle Drawer").performClick()

        rule.waitForIdle()

        assertTrue(drawerState.isOpen)
    }

    @Test
    fun appDrawer_renderedContent_expansionProgress() {
        rule.mainClock.autoAdvance = false
        val drawerState = DrawerState(DrawerValue.Closed)

        rule.setContent {
            AppDrawer(drawerState = drawerState) { progress, toggleDrawer ->
                Column {
                    Text(text = "Progress: $progress")
                    Button(onClick = toggleDrawer) { Text(text = "Toggle Drawer") }
                }
            }
        }

        rule.onNodeWithText("Progress: 0.0").assertExists()

        rule.onNodeWithText("Toggle Drawer").performClick()

        // This is coupled to implementation details of `DrawerState`, but we need to make sure
        // the values are calculated properly, and there's no way to inverse control of animation duration
        rule.mainClock.advanceTimeBy(128L)
        rule.onNode(hasText("Progress: 0.683", true)).assertExists()

        // We only need the second half of the animation duration here, but since it's rounded to be
        // a multiple of frame duration, we're using the full duration to make sure the animation definitely ends
        rule.mainClock.advanceTimeBy(256L)
        rule.onNodeWithText("Progress: 1.0").assertExists()
    }

    @Test
    fun appDrawer_shouldProvideStateToDescendants() {
        rule.setContent {
            AppDrawer { _, toggleDrawer ->
                Column {
                    val drawerState = LocalAppDrawerState.current
                    Text("Drawer is ${if (drawerState.isOpen) "open" else "closed"}")
                    Button(onClick = toggleDrawer) { Text(text = "Toggle Drawer") }
                }
            }
        }

        rule.onNodeWithText("Drawer is closed").assertExists()

        rule.onNodeWithText("Toggle Drawer").performClick()

        rule.onNodeWithText("Drawer is open").assertExists()
    }

    @Test
    fun appDrawer_backButtonHandler_shouldToggleDrawer() {
        val drawerState = DrawerState(DrawerValue.Open)
        rule.setContent {
            AppDrawer(drawerState = drawerState) { _, toggleDrawer ->
                Column {
                    val nestedDrawerState = LocalAppDrawerState.current
                    Text("Drawer is ${if (nestedDrawerState.isOpen) "open" else "closed"}")

                    HandleAppDrawerBackButton()
                }
            }
        }

        rule.onNodeWithText("Drawer is open").assertExists()

        rule.activity.onBackPressedDispatcher.onBackPressed()
        rule.waitForIdle()

        rule.onNodeWithText("Drawer is closed").assertExists()
    }
}
