import kotlinx.datetime.Instant

data class SmsData(val sender: String, val timestamp: Instant, val message: String)