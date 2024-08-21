package io.mrnateriver.smsproxy.relay

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.PowerManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.mrnateriver.smsproxy.relay.services.MessageProcessingWorkerService
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider {
    private lateinit var wakeLock: PowerManager.WakeLock

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @SuppressLint("WakelockTimeout")
    override fun onCreate() {
        super.onCreate()

        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MainApplication::WakelockTag").apply {
                acquire()
            }
        }

        // Process any forgotten messages on startup
        MessageProcessingWorkerService.scheduleBackgroundWork(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        wakeLock.release()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}