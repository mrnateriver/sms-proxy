package io.mrnateriver.smsproxy.relay.permissions

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

private const val PERMISSION_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS
private const val PERMISSION_READ_SMS = Manifest.permission.READ_SMS

enum class PermissionState {
    UNKNOWN,
    GRANTED,
    DENIED
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberSmsPermissions(): PermissionState {
    var result by remember { mutableStateOf(PermissionState.UNKNOWN) }
    val state = rememberMultiplePermissionsState(
        permissions = listOf(
            PERMISSION_RECEIVE_SMS,
            PERMISSION_READ_SMS
        )
    ) { userSelection ->
        result = when {
            userSelection.values.all { it } -> PermissionState.GRANTED
            else -> PermissionState.DENIED
        }
    }

    LaunchedEffect(Unit) {
        state.launchMultiplePermissionRequest()
    }

    return result
}