package io.mrnateriver.smsproxy.relay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.mrnateriver.smsproxy.relay.layout.AppLayout
import io.mrnateriver.smsproxy.relay.layout.AppNavHost
import io.mrnateriver.smsproxy.relay.layout.drawer.AppDrawerContents
import io.mrnateriver.smsproxy.relay.pages.home.isHomePageRoute
import io.mrnateriver.smsproxy.shared.composables.AppPreferencesProvider
import io.mrnateriver.smsproxy.shared.composables.theme.AppMaterialTheme

@Composable
fun App() {
    AppMaterialTheme {
        AppPreferencesProvider {
            val navController = rememberNavController()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val activePage = AppPages.fromNavDestination(currentDestination)

            AppLayout(
                title = stringResource(activePage?.descriptor?.titleResId ?: R.string.app_name),
                isHomePage = isHomePageRoute(currentDestination),
                onNavigateUpClick = { navController.navigateUp() },
                drawerContent = { toggleDrawer ->
                    AppDrawerContents(
                        activePage = activePage,
                        toggleDrawer = toggleDrawer,
                        onNavigateClick = navigate(toggleDrawer, navController),
                    )
                },
                content = {
                    AppNavHost(navController = navController)
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

    val nav = route.descriptor.navigate
    navController?.nav {
        if (route.descriptor.popUpToRoot) {
            popUpTo(navController.graph.findStartDestination().id)
        }
        launchSingleTop = true
    }
}
