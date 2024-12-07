package io.mrnateriver.smsproxy.server.framework

import io.ktor.util.logging.Logger
import io.sentry.ILogger
import io.sentry.SentryLevel
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

class SentryLogger : ILogger {
    private val logger: Logger = LoggerFactory.getLogger(SentryLogger::class.java)

    override fun log(level: SentryLevel, message: String, vararg args: Any?) {
        logger.atLevel(level.toSL4JLevel()).log(message, *args)
    }

    override fun log(level: SentryLevel, message: String, throwable: Throwable?) {
        logger.atLevel(level.toSL4JLevel()).setCause(throwable).log(message)
    }

    override fun log(level: SentryLevel, throwable: Throwable?, message: String, vararg args: Any?) {
        logger.atLevel(level.toSL4JLevel()).setCause(throwable).log(message, *args)
    }

    override fun isEnabled(level: SentryLevel?): Boolean {
        return logger.isEnabledForLevel(level?.toSL4JLevel() ?: Level.ERROR)
    }

    private fun SentryLevel.toSL4JLevel(): Level {
        return when (this) {
            SentryLevel.DEBUG -> Level.DEBUG
            SentryLevel.INFO -> Level.INFO
            SentryLevel.WARNING -> Level.WARN
            SentryLevel.ERROR -> Level.ERROR
            SentryLevel.FATAL -> Level.ERROR
        }
    }
}
