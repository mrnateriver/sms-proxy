package io.mrnateriver.smsproxy.shared.services

import android.util.Log
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import java.util.logging.Level

// TODO: real observability, for instance Sentry; rename the class to something like "SentryObservabilityService"
class AndroidObservabilityService : ObservabilityService {
    override fun log(level: Level, message: String) {
        Log.println(mapLogLevel(level), "shared", message)
    }

    override fun reportException(exception: Throwable) {
        log(Level.SEVERE, exception.stackTraceToString())
    }

    override suspend fun <T> runSpan(name: String, body: suspend () -> T): T {
        log(Level.FINE, "Starting span: $name")
        val result = body()
        log(Level.FINE, "Ending span: $name")
        return result
    }

    override suspend fun incrementCounter(metricName: String) {
        // TODO: actually report metrics
        log(Level.FINE, "Incrementing counter: $metricName")
    }

    private fun mapLogLevel(level: Level): Int {
        return when (level) {
            Level.SEVERE -> Log.ERROR
            Level.WARNING -> Log.WARN
            Level.INFO -> Log.INFO
            Level.CONFIG -> Log.VERBOSE
            Level.FINE -> Log.DEBUG
            Level.FINER -> Log.DEBUG
            Level.FINEST -> Log.DEBUG
            else -> Log.DEBUG
        }
    }

}