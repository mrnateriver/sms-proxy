package io.mrnateriver.smsproxy.relay.services.usecases.contracts

interface MessageBackgroundProcessingService {
    suspend fun handleUnprocessedMessages(): MessageBackgroundProcessingResult

    enum class MessageBackgroundProcessingResult {
        SUCCESS,
        FAILURE,
        RETRY,
    }
}
