package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.shared.composables.AlertMessage

@Preview
@Composable
fun ApiSettingsWarningCard(modifier: Modifier = Modifier) {
    AlertMessage(
        modifier = modifier,
        text = stringResource(R.string.settings_page_address_and_key_warning_message),
        textIconVector = Icons.Outlined.Info
    )
}