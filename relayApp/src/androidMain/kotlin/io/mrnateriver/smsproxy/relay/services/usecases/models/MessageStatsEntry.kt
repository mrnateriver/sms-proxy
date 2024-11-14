package io.mrnateriver.smsproxy.relay.services.usecases.models

import kotlinx.datetime.LocalDateTime

data class MessageStatsEntry(
    val value: Int = 0,
    val lastEvent: LocalDateTime? = null,
)
