package io.mrnateriver.smsproxy.server.data

import io.mrnateriver.smsproxy.shared.contracts.LogLevel
import javax.inject.Inject
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class ObservabilityService @Inject constructor() : ObservabilityServiceContract {
    // TODO: OTEL for spans and metrics, logging with some famous kotlin/java library

    override fun log(level: LogLevel, message: String) {
        println("[$level] $message")
    }

    override fun reportException(exception: Throwable) {
        println("Exception: ${exception.message}")
        exception.printStackTrace()
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
}
