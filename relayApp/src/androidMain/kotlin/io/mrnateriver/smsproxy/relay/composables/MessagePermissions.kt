package io.mrnateriver.smsproxy.relay.composables

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

private const val PERMISSION_RECEIVE_MESSAGES = Manifest.permission.RECEIVE_SMS
private const val PERMISSION_READ_MESSAGES = Manifest.permission.READ_SMS

enum class PermissionStatus {
    UNKNOWN,
    GRANTED,
    DENIED
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun rememberMessagePermissions(): State<PermissionStatus> {
    var gotUserResponse by remember { mutableStateOf(false) }
    val state = rememberMultiplePermissionsState(
        permissions = listOf(
            PERMISSION_RECEIVE_MESSAGES,
            PERMISSION_READ_MESSAGES,
        )
    ) { gotUserResponse = true }

    LaunchedEffect(Unit) {
        state.launchMultiplePermissionRequest()
    }

    return remember {
        derivedStateOf {
            getDerivedPermissionState(gotUserResponse, state.allPermissionsGranted)
        }
    }
}

internal fun getDerivedPermissionState(
    gotUserResponse: Boolean,
    allPermissionsGranted: Boolean,
): PermissionStatus {
    return when {
        !gotUserResponse -> PermissionStatus.UNKNOWN
        allPermissionsGranted -> PermissionStatus.GRANTED
        else -> PermissionStatus.DENIED
    }
}