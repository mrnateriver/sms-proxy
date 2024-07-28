package io.mrnateriver.smsproxy.relay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.mrnateriver.smsproxy.relay.pages.home.isHomePageRoute
import io.mrnateriver.smsproxy.relay.layout.AppLayout
import io.mrnateriver.smsproxy.relay.layout.AppNavHost
import io.mrnateriver.smsproxy.relay.layout.AppPages
import io.mrnateriver.smsproxy.relay.layout.drawer.AppDrawerContents
import io.mrnateriver.smsproxy.shared.theme.AppMaterialTheme

@Preview
@Composable
fun App() {
    AppMaterialTheme {
        AppPreferencesProvider {
            val navController = rememberNavController()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val activePage = AppPages.fromNavDestination(currentDestination)

            val appViewModel = hiltViewModel<AppViewModel>()

            AppLayout(
                title = stringResource(activePage?.titleResId ?: R.string.app_name),
                isHomePage = isHomePageRoute(currentDestination),
                onNavigateUpClicked = { navController.navigateUp() },
                drawerContent = { toggleDrawer ->
                    AppDrawerContents(
                        activePage = activePage,
                        onNavigateClick = navigate(toggleDrawer, navController),
                    )
                },
                content = {
                    AppNavHost(
                        navController = navController,
                        appViewModel = appViewModel
                    )
                },
            )
        }
    }
}

private fun navigate(
    toggleDrawer: () -> Unit,
    navController: NavController?,
): (route: AppPages) -> Unit = { route ->
    toggleDrawer()

    val nav = route.navigate
    navController?.nav {
        if (route.popUpToRoot) {
            popUpTo(navController.graph.findStartDestination().id)
        }
        launchSingleTop = true
    }
}
