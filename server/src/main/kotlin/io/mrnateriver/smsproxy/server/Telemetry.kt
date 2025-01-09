package io.mrnateriver.smsproxy.server

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.Meter
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator
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
import java.util.concurrent.TimeUnit

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

    routing {
        get("test") {
            val tp = call.request.headers.get("traceparent")
            log.info("incoming traceparent: $tp")
            call.respondText("OK")
        }
    }

    install(KtorServerTelemetry) {
        setOpenTelemetry(openTelemetry)

        attributesExtractor {
            onStart {
                log.debug("request start context: $parentContext")
            }

            onEnd {
                log.debug("request end context: $parentContext")
            }
        }
    }

    return TelemetryServices(
        tracer = openTelemetry.getTracer(packageName),
        meter = openTelemetry.getMeter(packageName),
    )
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

        sdkBuilder.setTracerProvider(sdkTracerProvider)

        Runtime.getRuntime().addShutdownHook(Thread(sdkTracerProvider::close))
    }

    val sdkMeterProvider = SdkMeterProvider.builder()
        .registerMetricReader(PrometheusHttpServer.builder().setPort(config.metricsHttpPort).build())
        .setResource(serviceNameResource)
        .build()

    val sdk =
        sdkBuilder
            .setMeterProvider(sdkMeterProvider)
            .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
            .build()

    return sdk
}
