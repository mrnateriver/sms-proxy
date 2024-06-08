package io.mrnateriver.smsproxy.relay

import android.Manifest
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

enum class ReceiveSmsPermissionState {
    UNKNOWN,
    GRANTED,
    DENIED
}

private const val PERMISSION_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS

// TODO: tests!
// 1. returns UNKNOWN before running any callbacks
// 2. returns GRANTED if permission has already been granted
// 3. launches activity if permissions haven't been granted yet
// 4. returns GRANTED if permission hasn't been granted at first but then granted from the activity
// 5. returns DENIED if permission hasn't been granted at first and then denied
// 6. doesn't re-launch activity on recomposition

// FIXME: Android System only has 2 states - denied/granted. We have to show a UI to explain why we need the permission, and a button to actually request it
@Composable
fun rememberSmsPermissionState(): ReceiveSmsPermissionState {
    var permissionState by remember {
        mutableStateOf(ReceiveSmsPermissionState.UNKNOWN)
    }

    // TODO: show rationale?
    val receiveSmsPermissionResult = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        permissionState =
            if (it) ReceiveSmsPermissionState.GRANTED else ReceiveSmsPermissionState.DENIED
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (!hasGrantedPermission(context, PERMISSION_RECEIVE_SMS)) {
            receiveSmsPermissionResult.launch(PERMISSION_RECEIVE_SMS)
        } else {
            permissionState = ReceiveSmsPermissionState.GRANTED
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
