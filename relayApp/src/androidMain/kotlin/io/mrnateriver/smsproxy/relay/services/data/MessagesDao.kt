package io.mrnateriver.smsproxy.relay.services.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus

@Dao
abstract class MessagesDao {
    @Insert
    abstract suspend fun insert(entry: MessageEntity)

    @Update
    abstract suspend fun update(entry: MessageEntity)

    @Query("SELECT * FROM MessageEntity WHERE sendStatus IN (:statuses)")
    abstract suspend fun getAll(vararg statuses: MessageRelayStatus): List<MessageEntity>

    @Query("SELECT COUNT(guid) FROM MessageEntity")
    abstract suspend fun getCount(): Int

    @Query("SELECT COUNT(guid) FROM MessageEntity WHERE sendStatus IN (:statuses)")
    abstract suspend fun getCountByStatuses(vararg statuses: MessageRelayStatus): Int
}
