package io.mrnateriver.smsproxy.relay.services.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MessageDaoEntity::class], version = 2)
@TypeConverters(InstantTypeConverter::class)
abstract class MessageDaoDatabase : RoomDatabase() {
    abstract fun messagesDao(): MessageDao
}
