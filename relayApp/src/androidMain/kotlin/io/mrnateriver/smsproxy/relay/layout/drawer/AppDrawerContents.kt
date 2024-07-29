package io.mrnateriver.smsproxy.relay.layout.drawer

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.layout.AppPages

@Preview
@Composable
fun AppDrawerContents(
    activePage: AppPages? = null,
    toggleDrawer: () -> Unit = {},
    onNavigateClick: (route: AppPages) -> Unit = {},
) {
    for (page in listOf(AppPages.SETTINGS, AppPages.ABOUT)) {
        AppDrawerEntry(
            icon = page.icon,
            label = stringResource(page.titleResId),
            selected = page == activePage,
            onClick = { if (page == activePage) toggleDrawer() else onNavigateClick(page) },
        )
    }
}
