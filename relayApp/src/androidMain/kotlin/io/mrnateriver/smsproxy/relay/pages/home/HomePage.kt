package io.mrnateriver.smsproxy.relay.pages.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import io.mrnateriver.smsproxy.shared.models.MessageEntry

const val HomePageRoute = "/"

fun NavGraphBuilder.homePage(
    navController: NavController,
    viewModel: AppViewModel,
) {
    composable(HomePageRoute) {
        val showApiKeyError = viewModel.showApiKeyError
        val messagePermissionsStatus by rememberMessagePermissions()
        val showSettingsHint by viewModel.showServerSettingsHint.collectAsStateWithLifecycle(false)
        val messageRecent by viewModel.messageRecordsRecent.collectAsStateWithLifecycle(listOf())
        val messageStats by viewModel.messageStats.collectAsStateWithLifecycle(MessageStatsData())

        HomePage(
            showApiKeyError = showApiKeyError,
            showApiSettingsHint = showSettingsHint,
            messagePermissionsStatus = messagePermissionsStatus,
            messageStatsData = messageStats,
            messageRecordsRecent = messageRecent,
            onGoToSettingsClick = { navController.navigateToSettingsPage() },
        )
    }
}

fun NavController.navigateHome() {
    popBackStack(graph.findStartDestination().id, inclusive = false, saveState = true)
}

fun isHomePageRoute(dest: NavDestination?): Boolean = dest?.route == HomePageRoute

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    onGoToSettingsClick: () -> Unit = {},
    showApiKeyError: Boolean = true,
    showApiSettingsHint: Boolean = true,
    messagePermissionsStatus: PermissionStatus = PermissionStatus.UNKNOWN,
    messageStatsData: MessageStatsData = MessageStatsData(),
    messageRecordsRecent: List<MessageEntry> = listOf(),
) {
    AppContentSurface {
        Dashboard(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars),
            onGoToSettingsClick = onGoToSettingsClick,
            showApiKeyError = showApiKeyError,
            showApiSettingsHint = showApiSettingsHint,
            messagePermissionStatus = messagePermissionsStatus,
            messageStatsData = messageStatsData,
            messageRecordsRecent = messageRecordsRecent,
        )
    }
}

@Preview
@Composable
private fun HomePagePreview() {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp)
    ) {
        HomePage()
    }
}