package io.mrnateriver.smsproxy.server

import io.ktor.server.application.Application
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.header
import io.ktor.util.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.Meter
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator
import io.opentelemetry.context.Context
import io.opentelemetry.context.propagation.ContextPropagators
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.exporter.prometheus.PrometheusHttpServer
import io.opentelemetry.instrumentation.ktor.v3_0.KtorServerTelemetry
import io.opentelemetry.instrumentation.micrometer.v1_5.OpenTelemetryMeterRegistry
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.metrics.SdkMeterProvider
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor
import io.opentelemetry.semconv.ServiceAttributes
import io.sentry.BaggageHeader
import io.sentry.Sentry
import io.sentry.SentryTraceHeader
import io.sentry.TransactionContext
import io.sentry.TransactionOptions
import java.util.concurrent.TimeUnit
import io.opentelemetry.api.trace.Span as OpenTelemetrySpan
import io.sentry.ISpan as SentrySpan

private const val OTLP_GRPC_PUSH_TIMEOUT_SECONDS = 30L

data class TelemetryServices(
    val tracer: Tracer,
    val meter: Meter,
)

fun Application.installTelemetry(config: TelemetryConfiguration): TelemetryServices {
    val openTelemetry = initOpenTelemetry(config)
    val packageName = ::main.javaClass.packageName

    val meterRegistry = OpenTelemetryMeterRegistry.builder(openTelemetry).build()
    install(MicrometerMetrics) {
        registry = meterRegistry
    }

    // Required due to:
    // https://youtrack.jetbrains.com/issue/KTOR-6802/Consistent-ThreadLocal-coroutine-context-leak-with-SuspendFunctionGun
    // https://github.com/Kotlin/kotlinx.coroutines/issues/2930
    System.setProperty("io.ktor.internal.disable.sfg", "true")

    install(KtorServerTelemetry) {
        setOpenTelemetry(openTelemetry)
    }

    install(ResponseTraceIdPlugin)
    install(SentryTracingPlugin)

    return TelemetryServices(
        tracer = openTelemetry.getTracer(packageName),
        meter = openTelemetry.getMeter(packageName),
    )
}

private val SentryTracingPlugin = createApplicationPlugin(name = "SentryTracingPlugin") {
    val requestSpanKey = AttributeKey<SentrySpan>("RequestSentrySpan")

    val logger = application.environment.log

    onCall { call ->
        val sentryTraceHeader = call.request.headers.get(SentryTraceHeader.SENTRY_TRACE_HEADER)

        @Suppress("UnstableApiUsage")
        val baggageHeader = call.request.headers.getAll(BaggageHeader.BAGGAGE_HEADER)

        val txOptions = TransactionOptions()
        txOptions.isBindToScope = true

        val txName = "${call.request.httpMethod.value} ${call.request.uri}"
        val txOperation = "http"

        var txContext = Sentry.continueTrace(sentryTraceHeader, baggageHeader)
        txContext = if (txContext != null) {
            logger.debug(
                "Request Sentry trace ID: {} span ID: {}",
                txContext.traceId,
                txContext.spanId,
            )

            txContext.name = txName
            txContext.operation = txOperation

            txContext
        } else {
            TransactionContext(txName, txOperation)
        }

        val parentSpan = Sentry.startTransaction(txContext, txOptions)
        call.attributes.put(requestSpanKey, parentSpan)
    }

    onCallRespond { call ->
        call.attributes.getOrNull(requestSpanKey)?.finish()
    }
}

private const val HEADER_TRACE_ID = "X-Trace-Id"

private val ResponseTraceIdPlugin = createApplicationPlugin(name = "ResponseTraceIdPlugin") {
    val logger = application.environment.log
    onCall { call ->
        call.request.headers.get("traceparent")?.let {
            logger.debug("Request traceparent: $it")
        }
    }

    onCallRespond { call ->
        Context.current()?.let {
            val spanContext = OpenTelemetrySpan.fromContext(it).getSpanContext()
            if (spanContext.isValid() && !call.response.headers.contains(HEADER_TRACE_ID)) {
                call.response.header(HEADER_TRACE_ID, spanContext.traceId)
            }
        }
    }
}

private fun initOpenTelemetry(config: TelemetryConfiguration): OpenTelemetrySdk {
    val sdkBuilder = OpenTelemetrySdk.builder()

    val serviceNameResource = Resource.create(Attributes.of(ServiceAttributes.SERVICE_NAME, "sms-proxy"))

    if (!config.otlpGrpcEndpoint.isNullOrBlank()) {
        val otlpExporter =
            OtlpGrpcSpanExporter.builder()
                .setEndpoint(config.otlpGrpcEndpoint)
                .setTimeout(OTLP_GRPC_PUSH_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()

        val sdkTracerProvider =
            SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.builder(otlpExporter).build())
                .setResource(serviceNameResource)
                .build()

        sdkBuilder
            .setTracerProvider(sdkTracerProvider)
            .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))

        Runtime.getRuntime().addShutdownHook(Thread(sdkTracerProvider::close))
    }

    val sdkMeterProvider = SdkMeterProvider.builder()
        .registerMetricReader(PrometheusHttpServer.builder().setPort(config.metricsHttpPort).build())
        .setResource(serviceNameResource)
        .build()

    val sdk =
        sdkBuilder
            .setMeterProvider(sdkMeterProvider)
            .build()

    return sdk
}
