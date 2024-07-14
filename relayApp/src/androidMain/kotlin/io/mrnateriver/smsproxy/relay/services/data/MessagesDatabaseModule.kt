package io.mrnateriver.smsproxy.relay.services.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class MessagesDatabaseModule {
    @Provides
    fun providesMessagesDatabase(@ApplicationContext context: Context): MessagesDatabase =
        Room.databaseBuilder(context, MessagesDatabase::class.java, "messages").build()

    @Provides
    fun providesMessagesDao(database: MessagesDatabase): MessagesDao = database.messagesDao()
}
