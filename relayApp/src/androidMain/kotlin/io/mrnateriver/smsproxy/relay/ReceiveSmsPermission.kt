package io.mrnateriver.smsproxy.relay

import android.Manifest
import androidx.compose.runtime.Composable

private const val PERMISSION_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS

@Composable
fun rememberSmsPermissionState(): PermissionState {
    return rememberPermissionState(PERMISSION_RECEIVE_SMS)
}
