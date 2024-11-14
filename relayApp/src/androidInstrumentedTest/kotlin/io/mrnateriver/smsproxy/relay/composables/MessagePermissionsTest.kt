package io.mrnateriver.smsproxy.relay.composables

import org.junit.Assert.assertTrue
import org.junit.Test

class MessagePermissionsTest {
    @Test
    fun rememberDerivedPermissionsState_shouldReturnUnknownStateWithoutUserResponse() {
        val result =
            getDerivedPermissionState(gotUserResponse = false, allPermissionsGranted = false)

        assertTrue(result == PermissionStatus.UNKNOWN)
    }

    @Test
    fun rememberDerivedPermissionsState_shouldReturnGrantedStatus() {
        val result = getDerivedPermissionState(gotUserResponse = true, allPermissionsGranted = true)

        assertTrue(result == PermissionStatus.GRANTED)
    }

    @Test
    fun rememberDerivedPermissionsState_shouldReturnDeniedStatus() {
        val result =
            getDerivedPermissionState(gotUserResponse = true, allPermissionsGranted = false)
        assertTrue(result == PermissionStatus.DENIED)
    }
}
