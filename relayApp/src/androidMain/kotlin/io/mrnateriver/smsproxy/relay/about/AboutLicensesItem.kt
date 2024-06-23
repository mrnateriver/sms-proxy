package io.mrnateriver.smsproxy.relay.about

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun AboutLicensesItem(modifier: Modifier = Modifier, navigateToLicensesPage: () -> Unit = {}) {
    AboutListItem(
        title = "Open Source Licenses", // TODO: i18n
        text = "Third-party software licenses", // TODO: i18n
        onClick = navigateToLicensesPage,
    )
    HorizontalDivider()
}