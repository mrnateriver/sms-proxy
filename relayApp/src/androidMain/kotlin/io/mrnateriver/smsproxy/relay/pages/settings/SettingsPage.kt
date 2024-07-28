package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import io.mrnateriver.smsproxy.relay.AppViewModel
import io.mrnateriver.smsproxy.relay.layout.AppContentSurface
import io.mrnateriver.smsproxy.relay.services.settings.SettingsService
import io.mrnateriver.smsproxy.shared.theme.AppSpacings
import kotlinx.coroutines.flow.map

private const val SettingsPageRoute = "settings"

fun NavGraphBuilder.settingsPage(navController: NavController, appViewModel: AppViewModel) {
    composable(SettingsPageRoute) {
        SettingsPage(navController, appViewModel.settingsService)
    }
}

fun NavController.navigateToSettingsPage(builder: (NavOptionsBuilder.() -> Unit)? = null) {
    navigate(SettingsPageRoute, if (builder == null) null else navOptions(builder))
}

fun isSettingsPageRoute(dest: NavDestination?): Boolean = dest?.route == SettingsPageRoute

@Composable
fun SettingsPage(navController: NavController, settingsService: SettingsService) {
    SettingsPageAppBarActions(navController)

    AppContentSurface {
        val serverAddressSet by settingsService.serverAddress.map { it.isNotBlank() }
            .collectAsState(false)
        val receiverKeySet by settingsService.receiverKey.map { it.isNotBlank() }
            .collectAsState(false)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (serverAddressSet || receiverKeySet) {
                item { ServerSettingsWarningCard(Modifier.padding(AppSpacings.medium)) }
            }

            serverAddressPreference(settingsService)
            receiverKeyPreference(settingsService)
            showRecentMessagesPreference(settingsService)
        }
    }
}
