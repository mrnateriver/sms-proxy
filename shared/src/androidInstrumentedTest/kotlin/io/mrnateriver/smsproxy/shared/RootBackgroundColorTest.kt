package io.mrnateriver.smsproxy.shared

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import io.mrnateriver.smsproxy.shared.composables.rememberRootBackgroundColor
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RootBackgroundColorTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun rememberRootBackgroundColor_darkMode_shouldReturnSurfaceContainerLowest() {
        var color: Color? = null
        var expected: Color? = null
        rule.setContent {
            expected = MaterialTheme.colorScheme.surfaceContainerLowest

            CompositionLocalProvider(
                LocalConfiguration provides Configuration().apply {
                    uiMode = Configuration.UI_MODE_NIGHT_YES
                },
            ) {
                color = rememberRootBackgroundColor()
            }
        }

        assertTrue(color == expected)
    }

    @Test
    fun rememberRootBackgroundColor_lightMode_shouldReturnPrimary() {
        var color: Color? = null
        var expected: Color? = null
        rule.setContent {
            color = rememberRootBackgroundColor()
            expected = MaterialTheme.colorScheme.primary
        }

        assertTrue(color == expected)
    }
}
