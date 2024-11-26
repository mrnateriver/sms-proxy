package io.mrnateriver.smsproxy.data

import app.cash.sqldelight.db.SqlDriver

interface MessagesDatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

// TODO: just refactor to Hikari pool directly
//fun createDatabase(driverFactory: MessagesDatabaseDriverFactory): MessagesDatabase {
//    val driver = driverFactory.createDriver()
//    return MessagesDatabase(
//        driver,
//        messagesAdapter = Messages.Adapter(
//            sendStatusAdapter = EnumColumnAdapter(),
//            messageDataAdapter = MessageDataDbAdapter(),
//        ),
//    )
//}
