package io.mrnateriver.smsproxy.server

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.mrnateriver.smsproxy.server.framework.installApi

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

