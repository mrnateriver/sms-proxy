package io.mrnateriver.smsproxy.shared.services

import android.util.Log
import io.mrnateriver.smsproxy.shared.contracts.LogLevel
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

// TODO: real observability, for instance Sentry; rename the class to something like "SentryObservabilityService"
class AndroidObservabilityService : ObservabilityServiceContract {
    override fun log(level: LogLevel, message: String) {
        Log.println(mapLogLevel(level), "shared", message)
    }

    override fun reportException(exception: Throwable) {
        log(LogLevel.ERROR, exception.stackTraceToString())
    }

    override suspend fun <T> runSpan(name: String, body: suspend () -> T): T {
        log(LogLevel.DEBUG, "Starting span: $name")
        val result = body()
        log(LogLevel.DEBUG, "Ending span: $name")
        return result
    }

    override suspend fun incrementCounter(metricName: String) {
        // TODO: actually report metrics
        log(LogLevel.DEBUG, "Incrementing counter: $metricName")
    }

    private fun mapLogLevel(level: LogLevel): Int {
        return when (level) {
            LogLevel.ERROR -> Log.ERROR
            LogLevel.WARNING -> Log.WARN
            LogLevel.INFO -> Log.INFO
            else -> Log.DEBUG
        }
    }
}
