package io.mrnateriver.smsproxy.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dagger.Module
import dagger.Provides
import javax.sql.DataSource

@Module
interface DataModule {
    companion object {
        @Provides
        fun provideDataSource(): DataSource {
            val config = HikariConfig()
            config.jdbcUrl = "jdbc:mysql://localhost:3306/simpsons"
            config.username = "bart"
            config.password = "51mp50n"
            config.addDataSourceProperty("cachePrepStmts", "true")
            config.addDataSourceProperty("prepStmtCacheSize", "250")
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

            return HikariDataSource(config)
        }

        @Provides
        fun provideSqlDriver(dataSource: DataSource): SqlDriver {
            return dataSource.asJdbcDriver()
        }
    }
}
