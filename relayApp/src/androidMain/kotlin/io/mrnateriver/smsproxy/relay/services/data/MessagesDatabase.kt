package io.mrnateriver.smsproxy.relay.services.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import javax.inject.Singleton

@Singleton
@Database(entities = [MessageEntity::class], version = 1)
@TypeConverters(InstantTypeConverter::class)
abstract class MessagesDatabase : RoomDatabase() {
    abstract fun messagesDao(): MessagesDao
}
