package io.mrnateriver.smsproxy.relay.drawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder
import io.mrnateriver.smsproxy.relay.about.isAboutPageRoute
import io.mrnateriver.smsproxy.relay.about.navigateToAboutPage
import io.mrnateriver.smsproxy.relay.settings.isSettingsPageRoute
import io.mrnateriver.smsproxy.relay.settings.navigateToSettingsPage

@Preview
@Composable
fun AppDrawerContents(
    navController: NavController? = null,
    currentDestination: NavDestination? = null,
    toggleDrawer: () -> Unit = {},
) {
    val switchPage = rememberSwitchPageCallback(toggleDrawer, navController)

    AppDrawerEntry(
        icon = Icons.Outlined.Settings,
        label = "Settings", // TODO: i18n
        selected = isSettingsPageRoute(currentDestination),
        onClick = { switchPage(NavController::navigateToSettingsPage) },
    )
    AppDrawerEntry(
        icon = Icons.Outlined.Info,
        label = "About", // TODO: i18n
        selected = isAboutPageRoute(currentDestination),
        onClick = { switchPage(NavController::navigateToAboutPage) },
    )
}

@Composable
private fun rememberSwitchPageCallback(
    toggleDrawer: () -> Unit,
    navController: NavController?,
): (nav: (NavController.(builder: (NavOptionsBuilder.() -> Unit)?) -> Unit)?) -> Unit {
    val switchPage: (nav: (NavController.(builder: (NavOptionsBuilder.() -> Unit)?) -> Unit)?) -> Unit =
        remember {
            { nav ->
                toggleDrawer()
                if (nav != null) {
                    navController?.nav {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }

    return switchPage
}
