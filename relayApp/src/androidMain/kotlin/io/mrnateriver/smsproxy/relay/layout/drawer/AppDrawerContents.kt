package io.mrnateriver.smsproxy.relay.layout.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.AppPages

@Composable
fun ColumnScope.AppDrawerContents(
    modifier: Modifier = Modifier,
    activePage: AppPages? = null,
    toggleDrawer: () -> Unit = {},
    onNavigateClick: (route: AppPages) -> Unit = {},
) {
    for (page in listOf(AppPages.SETTINGS, AppPages.ABOUT)) {
        val label = stringResource(page.descriptor.titleResId)
        AppDrawerEntry(
            modifier = modifier.semantics {
                testTag = "entry-${page.name}"
                contentDescription = "Link to $label page"
            },
            icon = page.descriptor.icon,
            label = label,
            selected = page == activePage,
            onClick = {
                if (page == activePage) {
                    toggleDrawer()
                } else {
                    onNavigateClick(page)
                }
            },
        )
    }
}

@Preview
@Composable
private fun AppDrawerContentsPreview() {
    Column {
        AppDrawerContents()
    }
}

@Preview("Active: Settings")
@Composable
private fun AppDrawerContentsPreview_Active_Settings() {
    Column {
        AppDrawerContents(activePage = AppPages.SETTINGS)
    }
}

@Preview("Active: About")
@Composable
private fun AppDrawerContentsPreview_Active_About() {
    Column {
        AppDrawerContents(activePage = AppPages.ABOUT)
    }
}
