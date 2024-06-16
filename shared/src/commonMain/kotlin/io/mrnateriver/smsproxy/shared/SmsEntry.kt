package io.mrnateriver.smsproxy.shared

import kotlinx.datetime.LocalDateTime
import java.util.UUID

data class SmsEntry(
    val guid: UUID,
    val internalId: String,
    val sendStatus: SmsRelayStatus,
    val sendRetries: UShort,
    val sendFailureReason: String?,
    val smsData: SmsData,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)