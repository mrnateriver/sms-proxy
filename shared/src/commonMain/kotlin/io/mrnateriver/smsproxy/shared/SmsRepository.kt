package io.mrnateriver.smsproxy.shared

import java.util.UUID

interface SmsRepository {
    suspend fun insert(entry: SmsData): SmsEntry
    suspend fun updateStatus(id: UUID, status: SmsRelayStatus)
    suspend fun getAll(vararg statuses: SmsRelayStatus): List<SmsEntry>
    suspend fun getById(id: UUID): SmsEntry?
    suspend fun getCount(): Int
}