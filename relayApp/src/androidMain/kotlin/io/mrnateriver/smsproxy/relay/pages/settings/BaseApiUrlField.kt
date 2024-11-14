package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.services.usecases.ApiBaseUrlValidationResult
import io.mrnateriver.smsproxy.relay.services.usecases.validateBaseApiUrl

fun LazyListScope.baseApiUrlPreference(
    value: String,
    onValueChange: (String) -> Unit = {},
) {
    item(
        key = "api-base-url-preferences-key",
        contentType = "ValidatedStringFieldPreference",
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val errorEmpty = stringResource(R.string.settings_page_entry_api_base_url_error_empty)
            val errorInvalid = stringResource(R.string.settings_page_entry_api_base_url_error_invalid_format)

            ValidatedStringFieldPreference(
                value = value,
                onValueChange = onValueChange,
                title = stringResource(R.string.settings_page_entry_api_base_url_title),
                validate = {
                    when (validateBaseApiUrl(it)) {
                        ApiBaseUrlValidationResult.VALID -> null
                        ApiBaseUrlValidationResult.INVALID_EMPTY -> errorEmpty
                        ApiBaseUrlValidationResult.INVALID_FORMAT -> errorInvalid
                    }
                },
            )
        }

        HorizontalDivider()
    }
}
