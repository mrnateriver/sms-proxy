package io.mrnateriver.smsproxy.relay.services

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.logging.Level
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "messages_stats")

val KEY_PROCESSING_FAILURES = intPreferencesKey("processing_failures")
val KEY_PROCESSING_FAILURE_TIMESTAMP = longPreferencesKey("processing_failure_timestamp")

@Singleton
class MessageStatsService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val observabilityService: ObservabilityService,
) {

    suspend fun incrementProcessingFailures() {
        context.dataStore.edit { preferences ->
            val currentFailures = preferences[KEY_PROCESSING_FAILURES] ?: 0
            val now = Clock.System.now()

            observabilityService.log(
                Level.FINEST,
                "Updating failure count: ${currentFailures + 1} at $now"
            )
            preferences[KEY_PROCESSING_FAILURES] = currentFailures + 1
            preferences[KEY_PROCESSING_FAILURE_TIMESTAMP] = now.toEpochMilliseconds()
        }
    }

    fun getProcessingFailures(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_PROCESSING_FAILURES] ?: 0
        }
    }

    fun getLastProcessingFailureTimestamp(): Flow<LocalDateTime?> {
        return context.dataStore.data.map { preferences ->
            val ts = preferences[KEY_PROCESSING_FAILURE_TIMESTAMP]
            if (ts != null) {
                Instant.fromEpochMilliseconds(ts).toLocalDateTime(TimeZone.currentSystemDefault())
            } else {
                null
            }
        }
    }

}