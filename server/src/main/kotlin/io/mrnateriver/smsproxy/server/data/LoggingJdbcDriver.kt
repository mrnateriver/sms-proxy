package io.mrnateriver.smsproxy.server.data

import app.cash.sqldelight.Query
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlPreparedStatement
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import io.ktor.util.logging.Logger
import java.sql.Connection
import javax.sql.DataSource

class LoggingJdbcDriver(private val dataSource: DataSource, private val logger: Logger) : JdbcDriver() {
    override fun getConnection(): Connection {
        return dataSource.connection
    }

    override fun closeConnection(connection: Connection) {
        connection.close()
    }

    override fun addListener(vararg queryKeys: String, listener: Query.Listener) {
        // No-op. JDBC Driver is not set up for observing queries by default.
    }

    override fun removeListener(vararg queryKeys: String, listener: Query.Listener) {
        // No-op. JDBC Driver is not set up for observing queries by default.
    }

    override fun notifyListeners(vararg queryKeys: String) {
        // No-op. JDBC Driver is not set up for observing queries by default.
    }

    override fun execute(
        identifier: Int?,
        sql: String,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?,
    ): QueryResult<Long> {
        logQuery(sql, binders)
        return super.execute(identifier, sql, parameters, binders)
    }

    override fun <R> executeQuery(
        identifier: Int?,
        sql: String,
        mapper: (SqlCursor) -> QueryResult<R>,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?,
    ): QueryResult<R> {
        logQuery(sql, binders)
        return super.executeQuery(identifier, sql, mapper, parameters, binders)
    }

    private fun logQuery(
        sql: String,
        binders: (SqlPreparedStatement.() -> Unit)?,
    ) {
        // TODO: SqlDelight generates checks for specific class of JdbcPreparedStatement, so running binders on a
        //       substitute to stringify param values is not possible; however, it might still be possible to inject
        //       a proxy in the execute/executeQuery methods to log the parameters before they are bound
        logger.debug("SQL: $sql")
    }
}

