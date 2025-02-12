package io.mrnateriver.smsproxy.shared.pages.about

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.shared.R

@Composable
fun AboutLicensesItem(modifier: Modifier = Modifier, navigateToLicensesPage: () -> Unit = {}) {
    AboutListItem(
        modifier = modifier,
        title = stringResource(R.string.about_page_entry_licenses_title),
        text = stringResource(R.string.about_page_entry_licenses_text),
        onClick = navigateToLicensesPage,
    )
    HorizontalDivider()
}

@Preview
@Composable
private fun AboutLicensesItemPreview() {
    AboutLicensesItem()
}
