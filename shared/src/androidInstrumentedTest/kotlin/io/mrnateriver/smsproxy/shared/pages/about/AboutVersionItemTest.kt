package io.mrnateriver.smsproxy.shared.pages.about

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class AboutVersionItemTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun aboutVersion_shouldShowProvidedVersionString() {
        rule.setContent {
            AboutVersionItem(versionString = "1.0.0.whatever")
        }

        rule.onNodeWithText("1.0.0.whatever").assertExists()
    }
}
