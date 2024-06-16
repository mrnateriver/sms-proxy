package io.mrnateriver.smsproxy.relay

import io.mrnateriver.smsproxy.shared.SmsData
import io.mrnateriver.smsproxy.shared.SmsEntry
import io.mrnateriver.smsproxy.shared.SmsRelayStatus
import java.util.UUID
import io.mrnateriver.smsproxy.shared.SmsRepository as SmsRepositoryContract

class SmsRepository : SmsRepositoryContract {
    override fun save(entry: SmsData): SmsEntry {
        TODO("Not yet implemented")
    }

    override fun getAll(vararg statuses: SmsRelayStatus): List<SmsEntry> {
        TODO("Not yet implemented")
    }

    override fun getById(id: UUID): SmsEntry? {
        TODO("Not yet implemented")
    }

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }
}