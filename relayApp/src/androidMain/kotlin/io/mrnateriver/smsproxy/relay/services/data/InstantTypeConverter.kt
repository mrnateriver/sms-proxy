package io.mrnateriver.smsproxy.relay.services.data

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

internal class InstantTypeConverter {
    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilliseconds()
}