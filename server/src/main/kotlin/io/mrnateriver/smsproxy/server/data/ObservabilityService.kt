package io.mrnateriver.smsproxy.server.data

import io.ktor.util.logging.Logger
import io.mrnateriver.smsproxy.shared.contracts.LogLevel
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.metrics.LongCounter
import io.opentelemetry.api.metrics.Meter
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.context.Context
import io.opentelemetry.extension.kotlin.asContextElement
import kotlinx.coroutines.withContext
import org.slf4j.event.Level
import javax.inject.Inject
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

class ObservabilityService @Inject constructor(
    private val tracer: Tracer?,
    private val meter: Meter?,
    private val logger: Logger,
) : ObservabilityServiceContract {
    private val cachedMeters: MutableMap<String, LongCounter> = mutableMapOf()

    override fun log(level: LogLevel, message: String, tag: String) {
        logger.atLevel(level).addKeyValue("tag", tag).log(message)
    }

    override fun reportException(exception: Throwable) {
        log(LogLevel.ERROR, "${exception.message}\n${exception.stackTraceToString()}")
    }

    override suspend fun <T> runSpan(name: String, attrs: Map<String, String>, body: suspend () -> T): T {
        if (tracer == null) {
            return body()
        }

        val context = Context.current()
        val spanBuilder = tracer.spanBuilder(name)
            .setSpanKind(SpanKind.INTERNAL)
            .setAttribute(AttributeKey.longKey("thread.id"), Thread.currentThread().threadId())

        for ((key, value) in attrs) {
            spanBuilder.setAttribute(key, value)
        }

        val span = spanBuilder.startSpan()
        val newContext = context.with(span)
        try {
            return withContext(newContext.asContextElement()) {
                body()
            }
        } catch (e: Throwable) {
            span.recordException(e)
            throw e
        } finally {
            span.end()
        }
    }

    override suspend fun incrementCounter(metricName: String) {
        if (meter == null) {
            return
        }

        cachedMeters.getOrPut(metricName) { meter.counterBuilder(metricName).build() }.add(1)
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
