package io.mrnateriver.smsproxy.server

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.callid.generate
import io.ktor.server.plugins.calllogging.CallLogging
import org.slf4j.event.Level

fun Application.installTracing() {
    install(CallId) {
        retrieveFromHeader(HttpHeaders.XRequestId)
        replyToHeader(HttpHeaders.XRequestId)
        generate(length = 32, dictionary = "0123456789abcdef")
    }

    install(CallLogging) {
        callIdMdc(HttpHeaders.XRequestId)
        level = Level.DEBUG
    }
}
