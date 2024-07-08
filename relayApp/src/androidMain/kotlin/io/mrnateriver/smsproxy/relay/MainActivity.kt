package io.mrnateriver.smsproxy.relay

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.mrnateriver.smsproxy.proxy.api.DefaultApi
import retrofit2.Retrofit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val rb = Retrofit.Builder().baseUrl("https://whatever.com").build()
        val svc = rb.create(DefaultApi::class.java)

        Log.d("MainActivity", "RETROFIT2 SERVICE CREATED: $svc")

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
