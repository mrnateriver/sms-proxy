package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.services.settings.validateReceiverKey

fun LazyListScope.receiverKeyPreference(state: MutableState<String>) {
    item(key = "api-receiver-key-preferences-key", contentType = "ValidatedStringFieldPreference") {
        Box(modifier = Modifier.fillMaxWidth()) {
            val resources = LocalContext.current.resources

            ValidatedStringFieldPreference(
                state = state,
                title = stringResource(R.string.settings_page_entry_receiver_key_title),
                validate = { validateReceiverKey(it, resources) },
            )
        }

        HorizontalDivider()
    }
}
