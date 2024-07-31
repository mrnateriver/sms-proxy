package io.mrnateriver.smsproxy.shared.contracts

import java.util.logging.Level

interface ObservabilityService {
    fun log(level: Level, message: String)
    fun reportException(exception: Throwable)
    suspend fun <T> runSpan(name: String, body: suspend () -> T): T
    suspend fun incrementCounter(metricName: String)
}