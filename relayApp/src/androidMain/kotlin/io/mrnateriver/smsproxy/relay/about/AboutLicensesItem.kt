package io.mrnateriver.smsproxy.relay.about

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R

@Preview
@Composable
fun AboutLicensesItem(modifier: Modifier = Modifier, navigateToLicensesPage: () -> Unit = {}) {
    AboutListItem(
        title = stringResource(R.string.about_page_entry_licenses_title),
        text = stringResource(R.string.about_page_entry_licenses_text),
        onClick = navigateToLicensesPage,
    )
    HorizontalDivider()
}