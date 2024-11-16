package io.mrnateriver.smsproxy.shared.pages.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.shared.R

@Composable
fun AboutVersionItem(modifier: Modifier = Modifier, versionString: String? = null) {
    AboutListItem(
        modifier = modifier,
        title = stringResource(R.string.about_page_entry_version_title),
        text = versionString.orEmpty(),
    )
}

@Preview
@Composable
private fun AboutVersionItemPreview() {
    AboutVersionItem()
}
