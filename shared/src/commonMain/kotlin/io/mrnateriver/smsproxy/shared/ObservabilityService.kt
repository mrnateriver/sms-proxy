package io.mrnateriver.smsproxy.shared

import java.util.logging.Level

interface ObservabilityService {
    fun log(level: Level, message: String)
    suspend fun <T> runSpan(name: String, body: suspend () -> T): T
}