package io.mrnateriver.smsproxy.relay

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
                NavHost(
                    navController = navController,
                    startDestination = HomePageRoute,
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(durationMillis = 500),
                        ) + scaleOut(
                            targetScale = 0.8f,
                            animationSpec = tween(durationMillis = 200),
                        )
                    },
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(durationMillis = 800),
                        ) + scaleIn(
                            initialScale = 0.8f,
                            animationSpec = tween(durationMillis = 1500),
                        )
                    },
                ) {
                    homePage()
                    aboutPage()
                    settingsPage()
                }
            },
        )
    }
}