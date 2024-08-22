package io.mrnateriver.smsproxy.relay.services.settings

enum class ReceiverKeyValidationResult {
    VALID,
    INVALID_FORMAT,
}

fun validateReceiverKey(value: String): ReceiverKeyValidationResult {
    return value.matches(RECEIVER_KEY_REGEX)
        .let { if (!it) ReceiverKeyValidationResult.INVALID_FORMAT else ReceiverKeyValidationResult.VALID }
}

private val RECEIVER_KEY_REGEX = Regex("""^[a-zA-Z0-9]{16}$""")
