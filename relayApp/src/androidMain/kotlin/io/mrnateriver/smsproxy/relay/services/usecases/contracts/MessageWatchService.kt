package io.mrnateriver.smsproxy.relay.services.usecases.contracts

import io.mrnateriver.smsproxy.shared.models.MessageEntry
import kotlinx.coroutines.flow.Flow

interface MessageWatchService {
    fun watchLastEntries(limit: Int): Flow<List<MessageEntry>>
}
