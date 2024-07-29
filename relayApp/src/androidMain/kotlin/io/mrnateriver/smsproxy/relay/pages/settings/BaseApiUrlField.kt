package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.composables.rememberMutableCoroutineState
import io.mrnateriver.smsproxy.relay.services.settings.SettingsService
import io.mrnateriver.smsproxy.relay.services.settings.validateBaseApiUrl

fun LazyListScope.baseApiUrlPreference(settingsService: SettingsService) {
    item(
        key = "api-base-url-preferences-key",
        contentType = "ValidatedStringFieldPreference"
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val state = rememberMutableCoroutineState(
                settingsService.baseApiUrl,
                settingsService::setBaseApiUrl,
                "",
            )

            val resources = LocalContext.current.resources

            ValidatedStringFieldPreference(
                state = state,
                title = stringResource(R.string.settings_page_entry_api_base_url_title),
                validate = { validateBaseApiUrl(it, resources) },
            )
        }

        HorizontalDivider()
    }
}
