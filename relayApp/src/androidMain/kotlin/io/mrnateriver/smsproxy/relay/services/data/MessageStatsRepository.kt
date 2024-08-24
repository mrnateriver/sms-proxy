package io.mrnateriver.smsproxy.relay.services.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import io.mrnateriver.smsproxy.relay.services.usecases.models.MessageStatsEntry
import io.mrnateriver.smsproxy.shared.contracts.LogLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageStatsRepository as MessageStatsRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

internal val KEY_PROCESSING_ERRORS = intPreferencesKey("processing_failures")
internal val KEY_PROCESSING_ERROR_TIMESTAMP = longPreferencesKey("processing_failure_timestamp")

class MessageStatsRepository(
    private val observabilityService: ObservabilityServiceContract,
    private val dataStore: DataStore<Preferences>,
    private val clock: Clock = Clock.System,
) : MessageStatsRepositoryContract {
    override suspend fun incrementProcessingErrors() {
        dataStore.edit { preferences ->
            val currentFailures = preferences[KEY_PROCESSING_ERRORS] ?: 0
            val now = clock.now()

            observabilityService.log(
                LogLevel.DEBUG,
                "Updating failure count: ${currentFailures + 1} at $now"
            )

            preferences[KEY_PROCESSING_ERRORS] = currentFailures + 1
            preferences[KEY_PROCESSING_ERROR_TIMESTAMP] = now.toEpochMilliseconds()
        }
    }

    override fun getProcessingErrors(): Flow<MessageStatsEntry> {
        return dataStore.data.map { preferences ->
            val value = preferences[KEY_PROCESSING_ERRORS] ?: 0
            val tsValue = preferences[KEY_PROCESSING_ERROR_TIMESTAMP]
            val ts = if (tsValue != null) {
                Instant.fromEpochMilliseconds(tsValue)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
            } else {
                null
            }

            MessageStatsEntry(value, ts)
        }
    }
}