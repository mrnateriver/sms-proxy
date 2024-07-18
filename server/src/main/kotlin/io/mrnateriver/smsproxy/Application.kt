package io.mrnateriver.smsproxy

import com.squareup.moshi.Json
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing

const val SERVER_PORT = 3000

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    routing {
        post<MessageProxyRequest>("/messages/proxy") {
            println("Request: $it")
            call.respondText("Ktor: hello", status = HttpStatusCode.NoContent)
        }
    }
}

// TODO: codegen
data class MessageProxyRequest(

    /* Random key of the end receiver of the proxied message. */
    @Json(name = "receiverKey")
    val receiverKey: kotlin.String,

    @Json(name = "sender")
    val sender: kotlin.String,

    @Json(name = "message")
    val message: kotlin.String,

    @Json(name = "receivedAt")
    val receivedAt: kotlinx.datetime.Instant,

    ) {


}
