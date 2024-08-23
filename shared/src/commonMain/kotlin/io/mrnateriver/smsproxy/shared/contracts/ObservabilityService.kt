package io.mrnateriver.smsproxy.shared.contracts

enum class LogLevel {
    DEBUG,
    INFO,
    WARNING,
    ERROR,
}

interface ObservabilityService {
    fun log(level: LogLevel, message: String)
    fun reportException(exception: Throwable)
    suspend fun <T> runSpan(name: String, body: suspend () -> T): T
    suspend fun incrementCounter(metricName: String)
}
