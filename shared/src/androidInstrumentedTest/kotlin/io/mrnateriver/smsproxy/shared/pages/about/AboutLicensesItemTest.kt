package io.mrnateriver.smsproxy.shared.pages.about

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AboutLicensesItemTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun aboutLicenses_shouldCallProvidedCallback() {
        var called = false
        rule.setContent { AboutLicensesItem(navigateToLicensesPage = { called = true }) }

        rule.onRoot().performClick()

        assertTrue(called)
    }
}