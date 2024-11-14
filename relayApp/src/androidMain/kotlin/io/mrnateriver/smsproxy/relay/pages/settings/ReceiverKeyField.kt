package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.services.usecases.ReceiverKeyValidationResult
import io.mrnateriver.smsproxy.relay.services.usecases.validateReceiverKey

fun LazyListScope.receiverKeyPreference(
    value: String,
    onValueChange: (String) -> Unit = {},
) {
    item(key = "api-receiver-key-preferences-key", contentType = "ValidatedStringFieldPreference") {
        Box(modifier = Modifier.fillMaxWidth()) {
            val validationError = stringResource(R.string.settings_page_entry_receiver_key_error_format)

            ValidatedStringFieldPreference(
                value = value,
                onValueChange = onValueChange,
                title = stringResource(R.string.settings_page_entry_receiver_key_title),
                validate = {
                    when (validateReceiverKey(it)) {
                        ReceiverKeyValidationResult.VALID -> null
                        ReceiverKeyValidationResult.INVALID_FORMAT -> validationError
                    }
                },
            )
        }

        HorizontalDivider()
    }
}
