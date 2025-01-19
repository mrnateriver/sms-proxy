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
import io.sentry.Sentry
import io.sentry.TransactionOptions
import io.sentry.kotlin.SentryContext
import kotlinx.coroutines.withContext
import org.slf4j.event.Level
import javax.inject.Inject
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract
import io.opentelemetry.api.trace.Span as OpenTelemetrySpan
import io.sentry.ISpan as SentrySpan
import io.sentry.SpanStatus as SentrySpanStatus

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

        val otelSpan = createOpenTelemetrySpan(name, attrs)
        val sentrySpan = createSentrySpan(name, attrs)

        val newContext = Context.current().with(otelSpan)
        try {
            return withContext(newContext.asContextElement() + SentryContext()) {
                body()
            }
        } catch (e: Throwable) {
            otelSpan.recordException(e)

            sentrySpan.throwable = e
            sentrySpan.status = SentrySpanStatus.INTERNAL_ERROR

            throw e
        } finally {
            sentrySpan.finish()
            otelSpan.end()
        }
    }

    override suspend fun incrementCounter(metricName: String) {
        if (meter == null) {
            return
        }

        val processedMetricName =
            "sms_proxy_${metricName.replace(Regex("[^a-zA-Z0-9_]"), "_").replace(Regex("^_+"), "_").lowercase()}"
        cachedMeters.getOrPut(processedMetricName) { meter.counterBuilder(processedMetricName).build() }.add(1)
    }

    private fun Logger.atLevel(level: LogLevel) = atLevel(
        when (level) {
            LogLevel.DEBUG -> Level.DEBUG
            LogLevel.INFO -> Level.INFO
            LogLevel.WARNING -> Level.WARN
            LogLevel.ERROR -> Level.ERROR
        },
    )

    private fun createOpenTelemetrySpan(name: String, attrs: Map<String, String>): OpenTelemetrySpan {
        val spanBuilder = tracer!!.spanBuilder(name)
            .setSpanKind(SpanKind.INTERNAL)
            .setAttribute(AttributeKey.longKey("thread.id"), Thread.currentThread().threadId())

        for ((key, value) in attrs) {
            spanBuilder.setAttribute(key, value)
        }

        return spanBuilder.startSpan()
    }

    private fun createSentrySpan(name: String, attrs: Map<String, String>): SentrySpan {
        val scopeSpan = Sentry.getSpan()
        val tx = if (scopeSpan == null) {
            log(LogLevel.DEBUG, "Creating Sentry transaction", "sentry")

            val txOptions = TransactionOptions()
            txOptions.isBindToScope = true

            Sentry.startTransaction(name, "runSpan", txOptions)
        } else {
            log(
                LogLevel.DEBUG,
                "Creating Sentry child span for trace ID: ${scopeSpan.spanContext.traceId}" +
                    " parent span ID: ${scopeSpan.spanContext.spanId}",
                "sentry",
            )

            scopeSpan.startChild(name)
        }

        for ((key, value) in attrs) {
            tx.setData(key, value)
        }

        return tx
    }
}
