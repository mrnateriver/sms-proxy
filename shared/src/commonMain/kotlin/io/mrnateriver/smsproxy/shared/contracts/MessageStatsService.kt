package io.mrnateriver.smsproxy.shared.contracts

interface MessageStatsService {
    fun incrementProcessingSuccesses()
    fun incrementProcessingErrors()
}
