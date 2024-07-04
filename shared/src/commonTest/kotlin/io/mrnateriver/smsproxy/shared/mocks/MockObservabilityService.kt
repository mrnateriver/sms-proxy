package io.mrnateriver.smsproxy.shared.mocks

import io.mrnateriver.smsproxy.shared.ObservabilityService
import java.util.logging.Level

class MockObservabilityService : ObservabilityService {
    private val _loggedMessages = mutableListOf<Pair<Level, String>>()
    private val _runSpans = mutableSetOf<String>()

    val loggedMessages: List<Pair<Level, String>>
        get() = _loggedMessages
    val runSpans: Set<String>
        get() = _runSpans

    fun reset() {
        _loggedMessages.clear()
        _runSpans.clear()
    }

    override fun log(level: Level, message: String) {
        _loggedMessages.add(level to message)
    }

    override suspend fun <T> runSpan(name: String, body: suspend () -> T): T {
        _runSpans.add(name)
        return body()
    }
}