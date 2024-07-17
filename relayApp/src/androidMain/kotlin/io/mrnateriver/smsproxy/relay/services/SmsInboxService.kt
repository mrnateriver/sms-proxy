package io.mrnateriver.smsproxy.relay.services

import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.annotation.IntRange
import dagger.hilt.android.qualifiers.ApplicationContext
import io.mrnateriver.smsproxy.shared.models.MessageData
import kotlinx.datetime.Instant
import javax.inject.Inject

class SmsInboxService @Inject constructor(@ApplicationContext private val context: Context) {
    fun getTotalSmsCount(): Int {
        return querySms { it.count } ?: 0
    }

    fun getRecentSms(@IntRange(from = 0) count: Int): List<MessageData> {
        return querySms { cursor ->
            val result = mutableListOf<MessageData>()
            if (cursor.moveToFirst()) {
                var i = 0
                do {
                    val messageData = MessageData(
                        sender = cursor.getString(1),
                        receivedAt = Instant.fromEpochMilliseconds(cursor.getLong(2)),
                        message = cursor.getString(3)
                    )
                    result.add(messageData)
                } while (cursor.moveToNext() && ++i < count)
            }
            result
        } ?: emptyList()
    }

    private fun <T> querySms(cb: (cursor: Cursor) -> T): T? {
        return context.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("_id", "address", "date", "body"),
            null,
            null,
            "date DESC"
        )?.use { cursor ->
            cb(cursor)
        }
    }
}