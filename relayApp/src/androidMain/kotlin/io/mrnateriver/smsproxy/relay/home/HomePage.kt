package io.mrnateriver.smsproxy.relay.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.mrnateriver.smsproxy.relay.layout.AppContentSurface
import io.mrnateriver.smsproxy.relay.permissions.PermissionState
import io.mrnateriver.smsproxy.relay.permissions.rememberSmsPermissions
import io.mrnateriver.smsproxy.relay.settings.navigateToSettingsPage
import io.mrnateriver.smsproxy.shared.models.MessageData
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

const val HomePageRoute = "/"

fun NavGraphBuilder.homePage(navController: NavController) {
    composable(HomePageRoute) {
        // TODO: move to an initialization service or something
        val receiveSmsPermissionResult = rememberSmsPermissions()

        // TODO: use a viewModel probably
        val smsRecords = remember {
            listOf(
                MessageData(
                    sender = "+12223334455",
                    message = "Hello World",
                    receivedAt = Clock.System.now(),
                ),
                MessageData(
                    sender = "Hello",
                    message = "General Kenobi",
                    receivedAt = Clock.System.now(),
                ),
                MessageData(
                    sender = "+993742732",
                    message = "Test",
                    receivedAt = Clock.System.now(),
                ),
                MessageData(
                    sender = "World",
                    message = "How's it going",
                    receivedAt = Clock.System.now(),
                ),
            )
        }

        // TODO: use a viewModel probably
        val smsStatsData = remember {
            SmsStatsData(
                received = 123,
                relayed = 456,
                errors = 0,
                failures = 0,
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                null,
            )
        }

        // TODO: check buildconfig
        val showApiKeyError = true

        HomePage(
            showApiKeyError = showApiKeyError,
            smsPermissionsState = receiveSmsPermissionResult,
            smsStatsData = smsStatsData,
            smsRecords = smsRecords,
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
    smsPermissionsState: PermissionState = PermissionState.UNKNOWN,
    smsStatsData: SmsStatsData = SmsStatsData(),
    smsRecords: List<MessageData> = listOf(),
) {
    AppContentSurface {
        Dashboard(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars),
            showApiKeyError = showApiKeyError,
            smsPermissionsState = smsPermissionsState,
            smsStatsData = smsStatsData,
            smsRecords = smsRecords,
            onGoToSettingsClick = onGoToSettingsClick,
        )
    }
}
