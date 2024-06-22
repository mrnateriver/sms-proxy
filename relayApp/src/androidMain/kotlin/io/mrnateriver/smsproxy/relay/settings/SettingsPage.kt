package io.mrnateriver.smsproxy.relay.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import io.mrnateriver.smsproxy.relay.AppContentSurface

private const val SettingsPageRoute = "settings"

fun NavGraphBuilder.settingsPage() {
    composable(SettingsPageRoute) {
        SettingsPage()
    }
}

fun NavController.navigateToSettingsPage(builder: (NavOptionsBuilder.() -> Unit)? = null) {
    navigate(SettingsPageRoute, if (builder == null) null else navOptions(builder))
}

fun isSettingsPageRoute(dest: NavDestination?): Boolean = dest?.route == SettingsPageRoute

@Preview
@Composable
fun SettingsPage() {
    AppContentSurface {
        Text(text = "Settings Page") // TODO: everything
    }
}
