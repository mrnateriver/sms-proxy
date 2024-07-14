package io.mrnateriver.smsproxy.shared

import android.util.Log
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import java.util.logging.Level

// TODO: real observability, for instance Sentry; rename the class to something like "SentryObservabilityService"
class AndroidObservabilityService : ObservabilityService {
    override fun log(level: Level, message: String) {
        Log.println(mapLogLevel(level), "shared", message)
    }

    override suspend fun <T> runSpan(name: String, body: suspend () -> T): T {
        log(Level.FINE, "Starting span: $name")
        val result = body()
        log(Level.FINE, "Ending span: $name")
        return result
    }

    private fun mapLogLevel(level: Level): Int {
        return when (level) {
            Level.SEVERE -> Log.ERROR
            Level.WARNING -> Log.WARN
            Level.INFO -> Log.INFO
            Level.CONFIG -> Log.DEBUG
            Level.FINE -> Log.VERBOSE
            Level.FINER -> Log.VERBOSE
            Level.FINEST -> Log.VERBOSE
            else -> Log.VERBOSE
        }
    }

}