package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.layout.appbar.rememberAppBarViewModel
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsPageAppBarActionsTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun settingsPageAppBarSaveAction_shouldCallProvidedOnBackClick() {
        var onBackClickCalled = false
        rule.setContent {
            SettingsPageAppBarActions {
                onBackClickCalled = true
            }
            val appBarViewModel = rememberAppBarViewModel()
            val actions by appBarViewModel.actions.collectAsState()
            Row {
                actions()
            }
        }

        rule.onNodeWithContentDescription(rule.activity.getString(R.string.settings_page_app_bar_action_save_label))
            .performClick()

        assertTrue(onBackClickCalled)
    }
}