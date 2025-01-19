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

internal val KEY_PROCESSING_SUCCESSES = intPreferencesKey("processing_successes")
internal val KEY_PROCESSING_SUCCESSES_TIMESTAMP = longPreferencesKey("processing_successes_timestamp")

class MessageStatsRepository(
    private val observabilityService: ObservabilityServiceContract,
    private val dataStore: DataStore<Preferences>,
    private val clock: Clock = Clock.System,
) : MessageStatsRepositoryContract {
    override suspend fun incrementProcessingErrors() {
        increment(KEY_PROCESSING_ERRORS, KEY_PROCESSING_ERROR_TIMESTAMP)
    }

    override suspend fun incrementProcessingSuccesses() {
        increment(KEY_PROCESSING_SUCCESSES, KEY_PROCESSING_SUCCESSES_TIMESTAMP)
    }

    override fun getProcessingErrors(): Flow<MessageStatsEntry> {
        return stats(KEY_PROCESSING_ERRORS, KEY_PROCESSING_ERROR_TIMESTAMP)
    }

    override fun getProcessingSuccesses(): Flow<MessageStatsEntry> {
        return stats(KEY_PROCESSING_SUCCESSES, KEY_PROCESSING_SUCCESSES_TIMESTAMP)
    }

    private suspend fun increment(keyVal: Preferences.Key<Int>, keyTs: Preferences.Key<Long>) {
        dataStore.edit { preferences ->
            val curVal = preferences[keyVal] ?: 0
            val now = clock.now()

            observabilityService.log(LogLevel.DEBUG, "Updating count: ${curVal + 1} at $now")

            preferences[keyVal] = curVal + 1
            preferences[keyTs] = now.toEpochMilliseconds()
        }
    }

    private fun stats(keyVal: Preferences.Key<Int>, keyTs: Preferences.Key<Long>): Flow<MessageStatsEntry> {
        return dataStore.data.map { preferences ->
            val value = preferences[keyVal] ?: 0
            val tsValue = preferences[keyTs]
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
