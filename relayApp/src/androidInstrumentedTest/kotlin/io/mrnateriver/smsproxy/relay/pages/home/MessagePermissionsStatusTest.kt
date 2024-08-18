package io.mrnateriver.smsproxy.relay.pages.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.composables.PermissionStatus
import org.junit.Rule
import org.junit.Test

class MessagePermissionsStatusTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun messagePermissionsStatus_unknown_shouldShowIndeterminateProgressBar() {
        rule.setContent {
            MessagePermissionsStatus(status = PermissionStatus.UNKNOWN)
        }

        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)).assertExists()
    }

    @Test
    fun messagePermissionsStatus_denied_shouldShowErrorAlert() {
        rule.setContent {
            MessagePermissionsStatus(status = PermissionStatus.DENIED)
        }

        rule.onNodeWithText(rule.activity.getString(R.string.dashboard_permissions_status_denied_title))
            .assertExists()
    }

    @Test
    fun messagePermissionsStatus_granted_shouldNotShowAnything() {
        rule.setContent {
            MessagePermissionsStatus(status = PermissionStatus.GRANTED)
        }

        rule.onNodeWithText(rule.activity.getString(R.string.dashboard_permissions_status_denied_title))
            .assertDoesNotExist()
        rule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertDoesNotExist()
    }
}