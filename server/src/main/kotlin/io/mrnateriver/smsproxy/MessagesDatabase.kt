package io.mrnateriver.smsproxy

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import io.mrnateriver.smsproxy.shared.Messages
import io.mrnateriver.smsproxy.shared.db.MessagesDatabase

interface MessagesDatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// TODO: just refactor to Hikari pool directly
fun createDatabase(driverFactory: MessagesDatabaseDriverFactory): MessagesDatabase {
    val driver = driverFactory.createDriver()
    return MessagesDatabase(
        driver,
        messagesAdapter = Messages.Adapter(
            sendStatusAdapter = EnumColumnAdapter(),
            messageDataAdapter = MessageDataDbAdapter(),
        ),
    )
}
