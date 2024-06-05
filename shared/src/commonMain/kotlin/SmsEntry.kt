import java.util.UUID // TODO: maybe https://github.com/hfhbd/kotlinx-uuid

data class SmsEntry(
    val guid: UUID,
    val sendStatus: SmsRelayStatus,
    val sendRetries: UShort,
    val sendFailureReason: String?,
    val smsData: SmsData
)