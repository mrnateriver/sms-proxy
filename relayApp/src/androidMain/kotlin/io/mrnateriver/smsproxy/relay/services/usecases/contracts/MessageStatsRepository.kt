package io.mrnateriver.smsproxy.relay.services.usecases.contracts

import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsEntry
import kotlinx.coroutines.flow.Flow

interface MessageStatsRepository {
    suspend fun incrementProcessingErrors()
    fun getProcessingErrors(): Flow<MessageStatsEntry>
}
