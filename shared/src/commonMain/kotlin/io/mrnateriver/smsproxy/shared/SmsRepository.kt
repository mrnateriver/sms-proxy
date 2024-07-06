package io.mrnateriver.smsproxy.shared

interface SmsRepository {
    suspend fun insert(entry: SmsData): SmsEntry
    suspend fun update(entry: SmsEntry): SmsEntry
    suspend fun getAll(vararg statuses: SmsRelayStatus): List<SmsEntry>
    suspend fun getCount(): Int
}