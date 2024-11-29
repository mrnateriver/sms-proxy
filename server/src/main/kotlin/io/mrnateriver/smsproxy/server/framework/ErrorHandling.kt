package io.mrnateriver.smsproxy.server.framework

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText

fun Application.installErrorHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            // TODO: handle validation or general http errors
            // TODO: output JSON
            // TODO: log error
            // TODO: metrics
            call.respondText(
                contentType = ContentType.Application.Json,
                status = HttpStatusCode.InternalServerError,
                text = cause.localizedMessage,
            )
        }
    }
}
