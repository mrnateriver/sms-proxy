package io.mrnateriver.smsproxy.relay.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import io.mrnateriver.smsproxy.relay.layout.AppContentSurface
import io.mrnateriver.smsproxy.shared.AppSpacings

private const val SettingsPageRoute = "settings"

fun NavGraphBuilder.settingsPage(navController: NavController) {
    composable(SettingsPageRoute) {
        SettingsPage(navController)
    }
}

fun NavController.navigateToSettingsPage(builder: (NavOptionsBuilder.() -> Unit)? = null) {
    navigate(SettingsPageRoute, if (builder == null) null else navOptions(builder))
}

fun isSettingsPageRoute(dest: NavDestination?): Boolean = dest?.route == SettingsPageRoute


@Composable
fun SettingsPage(navController: NavController) {
    SettingsPageAppBarActions(navController)

    AppContentSurface {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // TODO: check if settings are set
            item { ServerSettingsWarningCard(Modifier.padding(AppSpacings.medium)) }

            serverAddressPreference()
            receiverKeyPreference()
            showRecentMessagesPreference()
        }
    }
}
