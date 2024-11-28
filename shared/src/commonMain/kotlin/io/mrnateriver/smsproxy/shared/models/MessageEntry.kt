package io.mrnateriver.smsproxy.shared.models

import arrow.core.Either
import kotlinx.datetime.Instant
import java.util.UUID

data class MessageEntry(
    val guid: UUID,
    val externalId: String?,
    val sendStatus: MessageRelayStatus,
    val sendRetries: Int,
    val sendFailureReason: String?,
    val messageData: Either<MessageData, EncryptedMessageData>,
    val createdAt: Instant?,
    val updatedAt: Instant?,
)

typealias EncryptedMessageData = ByteArray
