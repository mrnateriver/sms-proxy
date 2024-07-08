package io.mrnateriver.smsproxy.relay.services

import io.mrnateriver.smsproxy.shared.SmsData
import io.mrnateriver.smsproxy.shared.SmsEntry
import io.mrnateriver.smsproxy.shared.SmsRelayStatus
import io.mrnateriver.smsproxy.shared.SmsRepository as SmsRepositoryContract

class SmsRepository : SmsRepositoryContract {
    override suspend fun insert(entry: SmsData): SmsEntry {
        TODO("Not yet implemented")
    }

    override suspend fun update(entry: SmsEntry): SmsEntry {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(vararg statuses: SmsRelayStatus): List<SmsEntry> {
        TODO("Not yet implemented")
    }

    override suspend fun getCount(): Int {
        TODO("Not yet implemented")
    }
}