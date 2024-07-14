package io.mrnateriver.smsproxy.relay

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.PowerManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    private lateinit var wakeLock: PowerManager.WakeLock

    @SuppressLint("WakelockTimeout")
    override fun onCreate() {
        super.onCreate()

        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MainApplication::WakelockTag").apply {
                acquire()
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        wakeLock.release()
    }
}