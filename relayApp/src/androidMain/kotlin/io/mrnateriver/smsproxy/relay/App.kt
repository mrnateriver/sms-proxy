package io.mrnateriver.smsproxy.relay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.mrnateriver.smsproxy.relay.about.aboutPage
import io.mrnateriver.smsproxy.relay.drawer.AppDrawerContents
import io.mrnateriver.smsproxy.relay.home.HomePageRoute
import io.mrnateriver.smsproxy.relay.home.homePage
import io.mrnateriver.smsproxy.relay.settings.settingsPage
import io.mrnateriver.smsproxy.shared.AppMaterialTheme

@Preview
@Composable
fun App() {
    AppMaterialTheme {
        val navController = rememberNavController()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        AppLayout(
            currentDestination = currentDestination,
            navigateUpClicked = { navController.navigateUp() },
            drawerContent = { toggleDrawer ->
                AppDrawerContents(
                    navController = navController,
                    currentDestination = currentDestination,
                    toggleDrawer = toggleDrawer,
                )
            },
            content = {
                NavHost(navController = navController, startDestination = HomePageRoute) {
                    homePage()
                    aboutPage()
                    settingsPage()
                }
            },
        )
    }
}