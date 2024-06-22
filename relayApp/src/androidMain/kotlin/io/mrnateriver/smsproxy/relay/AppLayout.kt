package io.mrnateriver.smsproxy.relay

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import io.mrnateriver.smsproxy.relay.drawer.AppDrawer
import io.mrnateriver.smsproxy.relay.home.isHomePageRoute

@Preview
@Composable
fun AppLayout(
    modifier: Modifier = Modifier,
    currentDestination: NavDestination? = null,
    navigateUpClicked: () -> Unit = {},
    drawerContent: @Composable (toggleDrawer: () -> Unit) -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    AppDrawer(
        modifier = modifier,
        drawerContent = drawerContent,
    ) { fraction, toggleDrawer ->
        val isHomePage = isHomePageRoute(currentDestination)

        val buttonClick: () -> Unit = remember(isHomePage) {
            {
                if (isHomePage) {
                    toggleDrawer()
                } else {
                    navigateUpClicked()
                }
            }
        }

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = MaterialTheme.colorScheme.primary,
            topBar = {
                AppBar(
                    isHomePage = isHomePage,
                    onMenuButtonClick = buttonClick,
                )
            },
        ) {
            AppContentSurface(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it),
                fraction = fraction,
                content = content,
            )
        }
    }
}
