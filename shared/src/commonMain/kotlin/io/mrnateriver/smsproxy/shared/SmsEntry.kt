package io.mrnateriver.smsproxy.shared

import kotlinx.datetime.Instant
import java.util.UUID

data class SmsEntry(
    val guid: UUID,
    val externalId: String,
    val sendStatus: SmsRelayStatus,
    val sendRetries: UShort,
    val sendFailureReason: String?,
    val smsData: SmsData,
    val createdAt: Instant?,
    val updatedAt: Instant?,
)