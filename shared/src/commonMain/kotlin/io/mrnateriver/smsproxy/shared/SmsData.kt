package io.mrnateriver.smsproxy.shared

import kotlinx.datetime.Instant

data class SmsData(val sender: String, val receivedAt: Instant, val message: String)