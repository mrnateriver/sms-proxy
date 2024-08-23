package io.mrnateriver.smsproxy.relay.services.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus

@Dao
abstract class MessageDao {
    @Insert
    abstract suspend fun insert(entry: MessageDaoEntity)

    @Update
    abstract suspend fun update(entry: MessageDaoEntity)

    @Query("SELECT * FROM MessageDaoEntity WHERE sendStatus IN (:statuses)")
    abstract suspend fun getAll(vararg statuses: MessageRelayStatus): List<MessageDaoEntity>

    @Query("SELECT * FROM MessageDaoEntity ORDER BY updatedAt DESC LIMIT :limit")
    abstract suspend fun getLastEntries(limit: Int): List<MessageDaoEntity>

    @Query("SELECT * FROM MessageDaoEntity WHERE sendStatus IN (:statuses) ORDER BY updatedAt DESC LIMIT 1")
    abstract suspend fun getLastEntryByStatus(vararg statuses: MessageRelayStatus): MessageDaoEntity?

    @Query("SELECT COUNT(guid) FROM MessageDaoEntity")
    abstract suspend fun getCount(): Int

    @Query("SELECT COUNT(guid) FROM MessageDaoEntity WHERE sendStatus IN (:statuses)")
    abstract suspend fun getCountByStatuses(vararg statuses: MessageRelayStatus): Int
}

