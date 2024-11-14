package io.mrnateriver.smsproxy.relay.layout.drawer

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppDrawerEntryTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun appDrawerEntry_shouldShowLabel() {
        rule.setContent {
            AppDrawerEntry(label = "Drawer Entry")
        }

        rule.onNodeWithText("Drawer Entry").assertExists()
    }

    @Test
    fun appDrawerEntry_shouldShowIcon() {
        rule.setContent {
            AppDrawerEntry(
                icon = Icons.Outlined.Settings,
                iconContentDescription = "Test Icon",
                label = "",
            )
        }

        rule.onNodeWithContentDescription("Test Icon").assertExists()
    }

    @Test
    fun appDrawerEntry_shouldSetSelectionState() {
        rule.setContent {
            AppDrawerEntry(
                modifier = Modifier.semantics { contentDescription = "Drawer Entry Root" },
                label = "Drawer Entry",
                selected = true,
            )
        }

        rule.onNodeWithContentDescription("Drawer Entry Root").assertIsSelected()
    }

    @Test
    fun appDrawerEntry_shouldInvokeOnClick() {
        var clicked = false
        rule.setContent {
            AppDrawerEntry(
                modifier = Modifier.semantics { contentDescription = "Drawer Entry Root" },
                label = "Drawer Entry",
                onClick = { clicked = true },
            )
        }

        rule.onNodeWithContentDescription("Drawer Entry Root").performClick()
        assertTrue(clicked)
    }
}
