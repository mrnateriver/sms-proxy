package io.mrnateriver.smsproxy.relay.pages.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.shared.composables.AlertMessage
import io.mrnateriver.smsproxy.shared.composables.AlertMessageType

@Preview
@Composable
fun ApiCertificatesStatus(modifier: Modifier = Modifier) {
    AlertMessage(
        modifier = modifier,
        type = AlertMessageType.ERROR,
        text = stringResource(R.string.home_page_missing_api_certificates_card_text),
        title = stringResource(R.string.home_page_missing_api_certificates_card_title),
    )
}