package io.mrnateriver.smsproxy.relay.settings

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
import io.mrnateriver.smsproxy.relay.services.settings.validateServerAddress

fun LazyListScope.serverAddressPreference(settingsService: SettingsService) {
    item(
        key = "api-server-address-preferences-key",
        contentType = "ValidatedStringFieldPreference"
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val state = rememberMutableCoroutineState(
                settingsService.serverAddress,
                settingsService::setServerAddress,
                "",
            )

            val resources = LocalContext.current.resources

            ValidatedStringFieldPreference(
                state = state,
                title = stringResource(R.string.settings_page_entry_server_address_title),
                validate = { validateServerAddress(it, resources) },
            )
        }

        HorizontalDivider()
    }
}
