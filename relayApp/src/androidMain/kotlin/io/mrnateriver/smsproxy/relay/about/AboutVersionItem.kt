package io.mrnateriver.smsproxy.relay.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.BuildConfig

@Preview
@Composable
fun AboutVersionItem(modifier: Modifier = Modifier) {
    AboutListItem(
        title = "Version", // TODO: i18n
        text = "${BuildConfig.VERSION_NAME}+${BuildConfig.VERSION_CODE}",
    )
}