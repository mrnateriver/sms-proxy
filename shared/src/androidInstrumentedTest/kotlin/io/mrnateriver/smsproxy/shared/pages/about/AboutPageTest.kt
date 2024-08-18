package io.mrnateriver.smsproxy.shared.pages.about

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class AboutPageTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun aboutPage_shouldRenderContentInTheProvidedWrapper() {
        rule.setContent {
            AboutPage { _ ->
                Box(modifier = Modifier.semantics { testTag = "Test Wrapper" })
            }
        }

        rule.onNodeWithTag("Test Wrapper").assertExists()
    }
}