package io.mrnateriver.smsproxy.shared

import java.util.UUID

interface SmsRepository {
    fun save(entry: SmsData): SmsEntry
    fun updateStatus(id: UUID, status: SmsRelayStatus)
    fun getAll(vararg statuses: SmsRelayStatus): List<SmsEntry>
    fun getById(id: UUID): SmsEntry?
    fun getCount(): Int
}