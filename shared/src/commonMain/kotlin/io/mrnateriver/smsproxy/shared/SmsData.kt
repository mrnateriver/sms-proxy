package io.mrnateriver.smsproxy.shared

import kotlinx.datetime.LocalDateTime

data class SmsData(val sender: String, val receivedAt: LocalDateTime, val message: String)