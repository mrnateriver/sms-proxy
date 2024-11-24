package io.mrnateriver.smsproxy

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val server = embeddedServer(
        Netty,
        configure = {
            configureConnection()
        },
        module = {
            installAuth()
            installApi()
        },
    )

    server.start(wait = true)
}

