package io.mrnateriver.smsproxy.relay.services

import io.mrnateriver.smsproxy.shared.SmsData
import io.mrnateriver.smsproxy.shared.SmsEntry
import io.mrnateriver.smsproxy.shared.SmsRelayStatus
import java.util.UUID
import io.mrnateriver.smsproxy.shared.SmsRepository as SmsRepositoryContract

class SmsRepository : SmsRepositoryContract {
    override suspend fun insert(entry: SmsData): SmsEntry {
        TODO("Not yet implemented")
    }

    override suspend fun incrementRetriesAndStartProgress(id: UUID): SmsEntry {
        TODO("Not yet implemented")
    }

    override suspend fun updateStatus(id: UUID, status: SmsRelayStatus, reason: String?): SmsEntry {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(vararg statuses: SmsRelayStatus): List<SmsEntry> {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: UUID): SmsEntry? {
        TODO("Not yet implemented")
    }

    override suspend fun getCount(): Int {
        TODO("Not yet implemented")
    }
}