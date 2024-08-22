package io.mrnateriver.smsproxy.relay.services.storage

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.relay.BuildConfig
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import java.util.concurrent.Executors
import java.util.logging.Level
import javax.inject.Singleton
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Provides
    @Singleton
    fun providesMessagesDatabase(
        @ApplicationContext context: Context,
        observabilityService: ObservabilityService,
    ): MessagesDatabase {
        val builder = Room.databaseBuilder(context, MessagesDatabase::class.java, "messages")

        if (BuildConfig.DEBUG) {
            builder.setQueryCallback({ sqlQuery, bindArgs ->
                if (sqlQuery.contains("TRANSACTION")) {
                    return@setQueryCallback
                }

                observabilityService.log(
                    Level.FINEST,
                    "SQL: $sqlQuery${if (bindArgs.isNotEmpty()) "\nArgs: $bindArgs" else ""}"
                )
            }, Executors.newSingleThreadExecutor())
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun providesMessagesDao(database: MessagesDatabase): MessagesDao = database.messagesDao()

    @Provides
    @Singleton
    fun providesMessageRepository(dao: MessagesDao): MessageRepositoryContract =
        MessageRepository(dao)
}
