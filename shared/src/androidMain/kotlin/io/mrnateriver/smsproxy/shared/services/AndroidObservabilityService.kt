package io.mrnateriver.smsproxy.shared.services

import android.util.Log
import io.mrnateriver.smsproxy.shared.contracts.LogLevel
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

open class AndroidObservabilityService : ObservabilityServiceContract {
    override fun log(level: LogLevel, message: String, tag: String) {
        Log.println(mapLogLevel(level), tag, message)
    }

    override fun reportException(exception: Throwable) {
        Log.println(Log.ERROR, "shared", exception.stackTraceToString())
    }

    override suspend fun <T> runSpan(name: String, body: suspend () -> T): T {
        log(LogLevel.DEBUG, "Starting span: $name")
        val result = body()
        log(LogLevel.DEBUG, "Ending span: $name")
        return result
    }

    override suspend fun incrementCounter(metricName: String) {
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
