package io.mrnateriver.smsproxy.relay.services.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.mrnateriver.smsproxy.relay.BuildConfig
import io.mrnateriver.smsproxy.shared.contracts.LogLevel
import java.util.concurrent.Executors
import javax.inject.Singleton
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.MessageStatsRepository as MessageStatsRepositoryContract
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.SettingsRepository as SettingsRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun providesMessagesDatabase(
        @ApplicationContext context: Context,
        observabilityService: ObservabilityServiceContract,
    ): MessageDaoDatabase {
        val builder = Room.databaseBuilder(context, MessageDaoDatabase::class.java, "messages")

        if (BuildConfig.DEBUG) {
            builder.setQueryCallback({ sqlQuery, bindArgs ->
                if (sqlQuery.contains("TRANSACTION")) {
                    return@setQueryCallback
                }

                observabilityService.log(
                    LogLevel.DEBUG,
                    "SQL: $sqlQuery${if (bindArgs.isNotEmpty()) "\nArgs: $bindArgs" else ""}",
                )
            }, Executors.newSingleThreadExecutor())
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun providesMessagesDao(database: MessageDaoDatabase): MessageDao = database.messagesDao()

    @Provides
    @Singleton
    fun providesMessageRepository(dao: MessageDao): MessageRepositoryContract =
        MessageRepository(dao)

    @Provides
    @Singleton
    fun providesSettingsRepository(@ApplicationContext context: Context): SettingsRepositoryContract =
        SettingsRepository(context.settingsStore)

    @Provides
    @Singleton
    fun providesMessageStatsRepository(
        @ApplicationContext context: Context,
        observabilityService: ObservabilityServiceContract,
    ): MessageStatsRepositoryContract =
        MessageStatsRepository(observabilityService, context.messageStatsStore)

    private val Context.messageStatsStore: DataStore<Preferences> by preferencesDataStore(name = "messages_stats")
    private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
}
