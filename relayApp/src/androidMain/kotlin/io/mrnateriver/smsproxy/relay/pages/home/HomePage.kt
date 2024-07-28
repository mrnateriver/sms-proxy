package io.mrnateriver.smsproxy.relay.pages.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.mrnateriver.smsproxy.relay.AppViewModel
import io.mrnateriver.smsproxy.relay.composables.PermissionStatus
import io.mrnateriver.smsproxy.relay.composables.rememberMessagePermissions
import io.mrnateriver.smsproxy.relay.layout.AppContentSurface
import io.mrnateriver.smsproxy.relay.pages.settings.navigateToSettingsPage
import io.mrnateriver.smsproxy.relay.services.MessageStatsData
import io.mrnateriver.smsproxy.shared.models.MessageData

const val HomePageRoute = "/"

fun NavGraphBuilder.homePage(
    navController: NavController,
    viewModel: AppViewModel,
) {
    composable(HomePageRoute) {
        val showApiKeyError = viewModel.showApiKeyError
        val messagePermissionsStatus by rememberMessagePermissions()
        val showServerSettingsHint by viewModel.showServerSettingsHint.collectAsStateWithLifecycle(false)
        val messageRecordsRecent by viewModel.messageRecordsRecent.collectAsStateWithLifecycle(listOf())
        val messageStats by viewModel.messageStats.collectAsStateWithLifecycle(MessageStatsData())

        HomePage(
            showApiKeyError = showApiKeyError,
            showServerSettingsHint = showServerSettingsHint,
            messagePermissionsStatus = messagePermissionsStatus,
            messageStatsData = messageStats,
            messageRecordsRecent = messageRecordsRecent,
            onGoToSettingsClick = { navController.navigateToSettingsPage() },
        )
    }
}

fun NavController.navigateHome() {
    popBackStack(graph.findStartDestination().id, inclusive = false, saveState = true)
}

fun isHomePageRoute(dest: NavDestination?): Boolean = dest?.route == HomePageRoute

@Preview
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    onGoToSettingsClick: () -> Unit = {},
    showApiKeyError: Boolean = true,
    showServerSettingsHint: Boolean = true,
    messagePermissionsStatus: PermissionStatus = PermissionStatus.UNKNOWN,
    messageStatsData: MessageStatsData = MessageStatsData(),
    messageRecordsRecent: List<MessageData> = listOf(),
) {
    AppContentSurface {
        Dashboard(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars),
            onGoToSettingsClick = onGoToSettingsClick,
            showApiKeyError = showApiKeyError,
            showServerSettingsHint = showServerSettingsHint,
            messagePermissionStatus = messagePermissionsStatus,
            messageStatsData = messageStatsData,
            messageRecordsRecent = messageRecordsRecent,
        )
    }
}
