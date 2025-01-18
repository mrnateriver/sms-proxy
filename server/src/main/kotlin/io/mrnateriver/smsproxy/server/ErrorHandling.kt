package io.mrnateriver.smsproxy.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.mrnateriver.smsproxy.server.entities.ApiError
import io.mrnateriver.smsproxy.server.entities.exceptions.ValidationException
import io.mrnateriver.smsproxy.server.framework.SentryLogger
import io.sentry.Sentry
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

fun initErrorHandling() {
    val logger = LoggerFactory.getLogger("UncaughtExceptionHandler")
    Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
        logger.error("${throwable.message}", throwable)
    }

    val sentryDsn = System.getenv("SENTRY_DSN")
    if (!sentryDsn.isNullOrBlank()) {
        Sentry.init { options ->
            options.dsn = sentryDsn
            options.tracesSampleRate = 1.0
            options.isDebug = ktorDevMode
            options.isPrintUncaughtStackTrace = true
            options.setLogger(SentryLogger())
        }
    }
}

fun Application.installErrorHandling(telemetryServices: TelemetryServices) {
    install(StatusPages) {
        // TODO: error metrics
        val meter = telemetryServices.meter

        status(HttpStatusCode.NotFound) { call, status ->
            call.respondApiError(
                code = HttpStatusCode.NotFound,
                message = "Not found",
                logLevel = Level.DEBUG,
            )
        }

        exception<Throwable> { call, cause ->
            Sentry.captureException(cause)
            call.respondApiError(
                code = HttpStatusCode.InternalServerError,
                message = (if (ktorDevMode) cause.message else null) ?: "Internal server error",
                logLevel = Level.ERROR,
                cause = cause,
            )
        }

        exception<BadRequestException> { call, cause ->
            call.respondApiError(
                code = HttpStatusCode.BadRequest,
                message = (if (ktorDevMode) cause.message else null) ?: "Bad request",
                logLevel = Level.WARN,
                cause = cause,
            )
        }

        exception<ValidationException> { call, cause ->
            call.respondApiError(
                code = HttpStatusCode.UnprocessableEntity,
                message = "Validation failed",
                logLevel = Level.WARN,
                errors = cause.errors,
                cause = cause,
            )
        }
    }
}

suspend fun ApplicationCall.respondApiError(
    code: HttpStatusCode,
    message: String,
    cause: Throwable? = null,
    logLevel: Level = Level.ERROR,
    errors: Map<String, List<String>>? = null,
) {
    application.environment.log.atLevel(logLevel).log(message, cause)
    respond(
        code,
        if (errors == null) {
            ApiError(code = code.value, message = message)
        } else {
            ApiError(code = code.value, message = message, errors = errors)
        },
    )
}
