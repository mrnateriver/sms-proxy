package io.mrnateriver.smsproxy.server.data

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.ktor.util.logging.Logger
import io.mrnateriver.smsproxy.server.ServerConfiguration
import io.mrnateriver.smsproxy.server.TelemetryServices
import io.mrnateriver.smsproxy.server.db.Database
import io.mrnateriver.smsproxy.server.db.Messages
import io.mrnateriver.smsproxy.server.db.MessagesQueries
import io.mrnateriver.smsproxy.server.db.ReceiversQueries
import javax.inject.Singleton
import javax.sql.DataSource
import io.mrnateriver.smsproxy.server.data.contracts.MessageEncrypter as MessageEncrypterContract
import io.mrnateriver.smsproxy.server.data.contracts.MessageSerializer as MessageSerializerContract
import io.mrnateriver.smsproxy.server.data.contracts.ReceiversRepository as ReceiversRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRelayService as MessageRelayServiceContract
import io.mrnateriver.smsproxy.shared.contracts.MessageRepository as MessageRepositoryContract
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService as ObservabilityServiceContract

@Module
interface DataModule {
    @Binds
    @Singleton
    fun bindMessagesRepository(messagesRepository: MessageRepository): MessageRepositoryContract

    @Binds
    @Singleton
    fun bindReceiversRepository(receiverRepository: ReceiversRepository): ReceiversRepositoryContract

    @Binds
    @Singleton
    fun bindMessageEncrypter(messageEncrypter: MessageEncrypter): MessageEncrypterContract

    @Binds
    @Singleton
    fun bindMessageSerializer(messageSerializer: MessageSerializer): MessageSerializerContract

    @Binds
    @Singleton
    fun bindMessageFirebaseRelay(messageFirebaseRelay: MessageFirebaseRelay): MessageRelayServiceContract

    companion object {
        @Provides
        @Singleton
        fun provideObservabilityService(
            telemetryServices: TelemetryServices?,
            logger: Logger,
        ): ObservabilityServiceContract {
            return ObservabilityService(telemetryServices?.tracer, telemetryServices?.meter, logger)
        }

        @Provides
        @Singleton
        fun provideDataSource(serverConfig: ServerConfiguration): DataSource {
            val config = HikariConfig()
            config.addDataSourceProperty("cachePrepStmts", "true")
            config.addDataSourceProperty("prepStmtCacheSize", "250")
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            config.jdbcUrl = serverConfig.db.url
            config.username = serverConfig.db.user
            config.password = serverConfig.db.password

            return HikariDataSource(config)
        }

        @Provides
        @Singleton
        fun provideSqlDriver(dataSource: DataSource, logger: Logger): SqlDriver {
            return LoggingJdbcDriver(dataSource, logger)
        }

        @Provides
        @Singleton
        fun provideDatabase(sqlDriver: SqlDriver): Database {
            return Database(
                driver = sqlDriver,
                messagesAdapter = Messages.Adapter(sendStatusAdapter = EnumColumnAdapter()),
            )
        }

        @Provides
        @Singleton
        fun provideMessagesQueries(db: Database): MessagesQueries {
            return db.messagesQueries
        }

        @Provides
        @Singleton
        fun provideReceiversQueries(db: Database): ReceiversQueries {
            return db.receiversQueries
        }
    }
}
