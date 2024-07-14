package io.mrnateriver.smsproxy.shared.models

import kotlinx.datetime.Instant
import java.util.UUID

data class MessageEntry(
    val guid: UUID,
    val externalId: String,
    val sendStatus: MessageRelayStatus,
    val sendRetries: UShort,
    val sendFailureReason: String?,
    val messageData: MessageData,
    val createdAt: Instant?,
    val updatedAt: Instant?,
)