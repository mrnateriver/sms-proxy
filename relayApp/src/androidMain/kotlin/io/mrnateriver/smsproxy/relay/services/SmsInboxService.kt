package io.mrnateriver.smsproxy.relay.services

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import io.mrnateriver.smsproxy.shared.models.MessageData
import kotlinx.datetime.Instant

@ActivityScoped
class SmsInboxService(@ActivityContext private val activityContext: Activity) {
    fun getTotalSmsCount(): Int {
        return querySms { it.count } ?: 0
    }

    fun getRecentSms(count: UInt): List<MessageData> {
        return querySms { cursor ->
            val result = mutableListOf<MessageData>()
            if (cursor.moveToFirst()) {
                var i = 0u
                do {
                    val messageData = MessageData(
                        internalId = cursor.getString(0),
                        sender = cursor.getString(1),
                        receivedAt = Instant.parse(cursor.getString(2)),
                        message = cursor.getString(3)
                    )
                    result.add(messageData)
                } while (cursor.moveToNext() && ++i < count)
            }
            result
        } ?: emptyList()
    }

    private fun <T> querySms(cb: (cursor: Cursor) -> T): T? {
        return activityContext.contentResolver.query(
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