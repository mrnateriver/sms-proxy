package io.mrnateriver.smsproxy.relay

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RelayApp()
        }

//        if (!hasReceiveSMSPermission()) {
//            requestPermissions()
//        }
    }

   /* private fun hasReceiveSMSPermission() =
        ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.RECEIVE_SMS
            )
        ) {
            requestPermissionsRationale()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                PERMISSIONS_REQUEST_RECEIVE_SMS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_RECEIVE_SMS) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_DENIED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.RECEIVE_SMS
                    )
                ) {
                    requestPermissionsRationale()
                } else {
                    requestPermissionsRationaleExplanation()
                }
            }
        }
    }

    private fun requestPermissionsRationale() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.label_permissions)
            .setMessage(R.string.label_permissions_message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                executePermissionRequest(
                    arrayOf(Manifest.permission.RECEIVE_SMS),
                    PERMISSIONS_REQUEST_RECEIVE_SMS
                )
            }
            .create()
            .show()
    }

    private fun requestPermissionsRationaleExplanation() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.label_permissions)
            .setMessage(R.string.label_permissions_message_mandatory)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                finish()
            }
            .create()
            .show()
    }

    companion object {
        private const val PERMISSIONS_REQUEST_RECEIVE_SMS = 100
    }*/
}

@Preview
@Composable
fun AppAndroidPreview() {
    RelayApp()
}