package io.mrnateriver.smsproxy.relay.services.usecases.contracts

import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsData
import kotlinx.coroutines.flow.Flow

interface MessageStatsService {
    val statsUpdates: Flow<Unit>

    fun triggerUpdate()
    suspend fun incrementProcessingErrors()
    fun getStats(): Flow<MessageStatsData>
}