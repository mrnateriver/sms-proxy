package io.mrnateriver.smsproxy.relay.services.usecases.contracts

import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsData
import kotlinx.coroutines.flow.Flow
import io.mrnateriver.smsproxy.shared.contracts.MessageStatsService as MessageStatsServiceContract

interface MessageStatsService : MessageStatsServiceContract {
    fun getStats(): Flow<MessageStatsData>
}
