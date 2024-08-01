package io.mrnateriver.smsproxy.relay.pages.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.shared.AlertMessage
import io.mrnateriver.smsproxy.shared.AlertMessageType

@Preview
@Composable
fun ApiKeyStatus(modifier: Modifier = Modifier) {
    AlertMessage(
        modifier = modifier,
        type = AlertMessageType.ERROR,
        text = stringResource(R.string.home_page_invalid_api_key_card_text),
        title = stringResource(R.string.home_page_invalid_api_key_card_title),
    )
}