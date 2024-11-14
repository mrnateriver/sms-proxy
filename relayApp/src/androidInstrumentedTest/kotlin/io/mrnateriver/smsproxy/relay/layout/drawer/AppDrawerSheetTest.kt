package io.mrnateriver.smsproxy.relay.layout.drawer

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import io.mrnateriver.smsproxy.relay.R
import org.junit.Rule
import org.junit.Test

class AppDrawerSheetTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun appDrawerSheet_shouldShowAppNameByDefault() {
        rule.setContent {
            AppDrawerSheet()
        }

        rule.onNodeWithText(rule.activity.getString(R.string.app_name)).assertExists()
    }

    @Test
    fun appDrawerSheet_shouldShowCustomContent() {
        val customContent = "Custom Content"
        rule.setContent {
            AppDrawerSheet {
                Box(modifier = Modifier.semantics { contentDescription = customContent })
            }
        }

        rule.onNodeWithContentDescription(customContent).assertExists()
    }
}
