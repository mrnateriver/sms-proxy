package io.mrnateriver.smsproxy.relay.services.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.datetime.Instant

@Database(entities = [MessageEntity::class], version = 2)
@TypeConverters(InstantTypeConverter::class)
abstract class MessagesDatabase : RoomDatabase() {
    abstract fun messagesDao(): MessagesDao
}

internal class InstantTypeConverter {
    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { Instant.fromEpochMilliseconds(it) }

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilliseconds()
}
