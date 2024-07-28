package io.mrnateriver.smsproxy.relay.services.storage

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import io.mrnateriver.smsproxy.shared.models.MessageData
import io.mrnateriver.smsproxy.shared.models.MessageRelayStatus
import kotlinx.datetime.Instant
import java.util.UUID

@Entity(indices = [Index(value = ["sendStatus"])])
data class MessageEntity(
    @PrimaryKey val guid: UUID,
    val externalId: String?,
    val sendStatus: MessageRelayStatus,
    val sendRetries: Int,
    val sendFailureReason: String?,
    @Embedded val messageData: MessageData,
    val createdAt: Instant?,
    val updatedAt: Instant?,
)