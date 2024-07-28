package io.mrnateriver.smsproxy.relay.pages.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.BuildConfig
import io.mrnateriver.smsproxy.relay.R

@Preview
@Composable
fun AboutVersionItem(modifier: Modifier = Modifier) {
    AboutListItem(
        title = stringResource(R.string.about_page_entry_version_title),
        text = "${BuildConfig.VERSION_NAME}+${BuildConfig.VERSION_CODE}",
    )
}