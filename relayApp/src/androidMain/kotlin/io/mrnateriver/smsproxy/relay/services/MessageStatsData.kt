package io.mrnateriver.smsproxy.relay.services

import androidx.compose.runtime.Stable
import kotlinx.datetime.LocalDateTime

@Stable
data class MessageStatsData(
    val processed: Int = 0,
    val relayed: Int = 0,
    val errors: Int = 0,
    val failures: Int = 0,
    val lastProcessedAt: LocalDateTime? = null,
    val lastRelayedAt: LocalDateTime? = null,
    val lastErrorAt: LocalDateTime? = null,
    val lastFailureAt: LocalDateTime? = null,
)
