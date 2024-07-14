package io.mrnateriver.smsproxy.shared.models

import kotlinx.datetime.Instant

data class MessageData(val sender: String, val receivedAt: Instant, val message: String)