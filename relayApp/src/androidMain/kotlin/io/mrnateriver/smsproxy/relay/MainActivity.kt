package io.mrnateriver.smsproxy.relay

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

//        val client = ApiClient("https://mrnateriver.io").setLogger { Log.d("ApiClient", it) }
//        val svc = client.createService(DefaultApi::class.java)
//
//        runBlocking {
//            try {
//                svc.recipientsRegister(
//                    RegisterRecipientRequest(
//                        "1234567890",
//                        "key",
//                        "value".toByteArray()
//                    )
//                )
//
//            } catch (e: Exception) {
//                Log.e("ApiClient", e.message.toString())
//            }
//        }

        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        enableEdgeToEdge()
    }

    private fun enableEdgeToEdge() {
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
    }
}
