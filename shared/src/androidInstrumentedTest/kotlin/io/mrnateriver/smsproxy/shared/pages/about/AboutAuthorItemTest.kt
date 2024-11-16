package io.mrnateriver.smsproxy.shared.pages.about

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock

class AboutAuthorItemTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun aboutAuthorItem_shouldOpenPredefinedURLOnClick() {
        var openedUri = false
        val uriHandler = mock<UriHandler> {
            on { openUri(any()) } doAnswer { openedUri = true }
        }

        rule.setContent {
            CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                AboutAuthorItem()
            }
        }

        rule.onRoot().performClick()

        assertTrue(openedUri)
    }
}
