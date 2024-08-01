package io.mrnateriver.smsproxy.relay.pages.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.shared.AlertMessage
import io.mrnateriver.smsproxy.shared.AlertMessageAction

@Preview
@Composable
fun ApiSettingsStatus(modifier: Modifier = Modifier, onGoToSettingsClick: () -> Unit = {}) {
    AlertMessage(
        modifier = modifier,
        text = stringResource(R.string.home_page_api_settings_card_text),
        title = stringResource(R.string.home_page_api_settings_card_title),
        action = AlertMessageAction(
            label = stringResource(R.string.home_page_api_settings_card_button_label),
            action = onGoToSettingsClick,
        ),
    )
}