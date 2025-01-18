package io.mrnateriver.smsproxy.server.data

import app.cash.sqldelight.Query
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlPreparedStatement
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import io.ktor.util.logging.Logger
import io.mrnateriver.smsproxy.shared.contracts.ObservabilityService
import kotlinx.coroutines.runBlocking
import java.sql.Connection
import javax.sql.DataSource

class LoggingJdbcDriver(
    private val dataSource: DataSource,
    private val logger: Logger,
    private val observabilityService: ObservabilityService,
) : JdbcDriver() {
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
        return runBlocking {
            observabilityService.runSpan(parseQuerySpanName(sql), mapOf("sql" to sql)) {
                logQuery(sql, binders)
                super.execute(identifier, sql, parameters, binders)
            }
        }
    }

    override fun <R> executeQuery(
        identifier: Int?,
        sql: String,
        mapper: (SqlCursor) -> QueryResult<R>,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?,
    ): QueryResult<R> {
        return runBlocking {
            observabilityService.runSpan(parseQuerySpanName(sql), mapOf("sql" to sql)) {
                logQuery(sql, binders)
                super.executeQuery(identifier, sql, mapper, parameters, binders)
            }
        }
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

    private fun parseQuerySpanName(sql: String): String {
        val lowercaseSql = sql.lowercase()
        if (lowercaseSql.contains("insert")) {
            return "insertDbQuery"
        } else if (lowercaseSql.contains("update")) {
            return "updateDbQuery"
        } else if (lowercaseSql.contains("delete")) {
            return "deleteDbQuery"
        } else if (lowercaseSql.contains("select")) {
            return "selectDbQuery"
        } else {
            return "unknownDbQuery"
        }
    }
}

