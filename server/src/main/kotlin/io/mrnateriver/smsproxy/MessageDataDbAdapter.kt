package io.mrnateriver.smsproxy

import app.cash.sqldelight.ColumnAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mrnateriver.smsproxy.shared.models.MessageData
import kotlinx.datetime.Instant

@OptIn(ExperimentalStdlibApi::class)
class MessageDataDbAdapter : ColumnAdapter<MessageData, String> {
    val jsonAdapter: JsonAdapter<MessageData> =
        Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build().adapter<MessageData>()

    override fun decode(databaseValue: String) = if (databaseValue.isEmpty()) {
        MessageData("", Instant.DISTANT_PAST, "")
    } else {
        jsonAdapter.fromJson(databaseValue) ?: error("Failed to parse JSON: $databaseValue")
    }

    override fun encode(value: MessageData) = jsonAdapter.toJson(value)
}
