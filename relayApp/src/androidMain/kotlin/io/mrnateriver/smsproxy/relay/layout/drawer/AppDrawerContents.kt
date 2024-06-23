package io.mrnateriver.smsproxy.relay.layout.drawer

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.layout.AppPages

@Preview
@Composable
fun AppDrawerContents(
    activePage: AppPages? = null,
    onNavigateClick: (route: AppPages) -> Unit = {},
) {
    for (page in listOf(AppPages.SETTINGS, AppPages.ABOUT)) {
        AppDrawerEntry(
            icon = page.icon,
            label = page.title,
            selected = page == activePage,
            onClick = { onNavigateClick(page) },
        )
    }
}
