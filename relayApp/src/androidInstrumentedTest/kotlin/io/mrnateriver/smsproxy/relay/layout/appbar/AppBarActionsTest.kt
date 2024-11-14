package io.mrnateriver.smsproxy.relay.layout.appbar

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Rule
import org.junit.Test

class AppBarActionsTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun appBar_shouldShowProvidedActions() {
        rule.setContent {
            AppBar()
            AppBarActions {
                IconButton(
                    modifier = Modifier.semantics { contentDescription = "test-button" },
                    onClick = { /* no-op */ },
                ) {
                    Icon(Icons.Outlined.Settings, contentDescription = "")
                }
            }
        }

        rule.onNodeWithContentDescription("test-button")
            .assertExists()
    }
}
