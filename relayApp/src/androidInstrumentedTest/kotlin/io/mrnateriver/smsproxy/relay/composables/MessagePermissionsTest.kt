package io.mrnateriver.smsproxy.relay.composables

import androidx.compose.ui.test.junit4.createComposeRule
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@OptIn(ExperimentalPermissionsApi::class)
class MessagePermissionsTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun rememberDerivedPermissionsState_shouldReturnUnknownStateWithoutUserResponse() {
        val mockState = mock<MultiplePermissionsState> {
            on { allPermissionsGranted } doReturn false
        }

        var result: PermissionStatus? = null
        rule.setContent {
            result =
                rememberDerivedPermissionsState(gotUserResponse = false, state = mockState).value
        }

        assertTrue(result == PermissionStatus.UNKNOWN)
    }

    @Test
    fun rememberDerivedPermissionsState_shouldReturnGrantedStatus() {
        val mockState = mock<MultiplePermissionsState> {
            on { allPermissionsGranted } doReturn true
        }

        var result: PermissionStatus? = null
        rule.setContent {
            result =
                rememberDerivedPermissionsState(gotUserResponse = true, state = mockState).value
        }

        assertTrue(result == PermissionStatus.GRANTED)
    }

    @Test
    fun rememberDerivedPermissionsState_shouldReturnDeniedStatus() {
        val mockState = mock<MultiplePermissionsState> {
            on { allPermissionsGranted } doReturn false
        }

        var result: PermissionStatus? = null
        rule.setContent {
            result =
                rememberDerivedPermissionsState(gotUserResponse = true, state = mockState).value
        }

        assertTrue(result == PermissionStatus.DENIED)
    }
}