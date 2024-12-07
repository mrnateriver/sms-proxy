package io.mrnateriver.smsproxy.server.data

import io.ktor.util.logging.Logger
import io.mrnateriver.smsproxy.shared.contracts.LogLevel
import org.slf4j.event.Level
import javax.inject.Inject
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class ObservabilityService @Inject constructor(private val logger: Logger) : ObservabilityServiceContract {
    // TODO: OTEL for spans and metrics

    override fun log(level: LogLevel, message: String, tag: String) {
        logger.atLevel(level).addKeyValue("tag", tag).log(message)
    }

    override fun reportException(exception: Throwable) {
        log(LogLevel.ERROR, "${exception.message}\n${exception.stackTraceToString()}")
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

    private fun Logger.atLevel(level: LogLevel) = atLevel(
        when (level) {
            LogLevel.DEBUG -> Level.DEBUG
            LogLevel.INFO -> Level.INFO
            LogLevel.WARNING -> Level.WARN
            LogLevel.ERROR -> Level.ERROR
        },
    )
}
