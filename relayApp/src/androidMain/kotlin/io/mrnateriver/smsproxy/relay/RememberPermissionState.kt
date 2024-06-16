package io.mrnateriver.smsproxy.relay

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

// TODO: tests!
// 1. returns UNKNOWN before running any callbacks
// 2. returns GRANTED if permission has already been granted
// 3. launches activity if permissions haven't been granted yet
// 4. returns GRANTED if permission hasn't been granted at first but then granted from the activity
// 5. returns DENIED if permission hasn't been granted at first and then denied
// 6. doesn't re-launch activity on recomposition

enum class PermissionState {
    UNKNOWN,
    GRANTED,
    DENIED
}

@Composable
fun rememberPermissionState(permission: String): PermissionState {
    var permissionState by remember {
        mutableStateOf(PermissionState.UNKNOWN)
    }

    val receiveSmsPermissionResult = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        permissionState =
            if (it) PermissionState.GRANTED else PermissionState.DENIED
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (!hasGrantedPermission(context, permission)) {
            receiveSmsPermissionResult.launch(permission)
        } else {
            permissionState = PermissionState.GRANTED
        }
    }

    return permissionState
}

fun hasGrantedPermission(
    context: Context,
    permission: String,
): Boolean {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
    return permissionCheckResult == PackageManager.PERMISSION_GRANTED
}
