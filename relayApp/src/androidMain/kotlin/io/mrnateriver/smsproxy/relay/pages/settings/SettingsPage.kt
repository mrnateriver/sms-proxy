package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import io.mrnateriver.smsproxy.relay.AppViewModel
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.composables.rememberMutableCoroutineState
import io.mrnateriver.smsproxy.relay.layout.AppContentSurface
import io.mrnateriver.smsproxy.shared.composables.AppPreferencesProvider
import io.mrnateriver.smsproxy.shared.composables.theme.AppSpacings
import io.mrnateriver.smsproxy.shared.pages.PageDescriptor
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.SettingsService as SettingsServiceContract

fun NavGraphBuilder.settingsPage(
    onBackClick: () -> Unit = {},
    viewModel: AppViewModel,
) {
    composable(SettingsPageRoute) {
        SettingsPage(onBackClick, viewModel.settingsService)
    }
}

fun NavController.navigateToSettingsPage(builder: (NavOptionsBuilder.() -> Unit)? = null) {
    navigate(SettingsPageRoute, if (builder == null) null else navOptions(builder))
}

val settingsPageDescriptor = PageDescriptor(
    Icons.Outlined.Settings,
    R.string.page_title_settings,
    NavController::navigateToSettingsPage,
    ::isSettingsPageRoute,
    true,
)

private const val SettingsPageRoute = "settings"

private fun isSettingsPageRoute(dest: NavDestination?): Boolean = dest?.route == SettingsPageRoute

@Composable
fun SettingsPage(onBackClick: () -> Unit = {}, settingsService: SettingsServiceContract) {
    SettingsPageAppBarActions(onBackClick)

    val apiConfigured by settingsService.isApiConfigured.collectAsStateWithLifecycle(false)
    val baseApiUrlState = rememberMutableCoroutineState(
        settingsService.baseApiUrl,
        settingsService::setBaseApiUrl,
        "",
    )
    val receiverKeyState = rememberMutableCoroutineState(
        settingsService.receiverKey,
        settingsService::setReceiverKey,
        ""
    )
    val showRecentMessagesState = rememberMutableCoroutineState(
        settingsService.showRecentMessages,
        settingsService::setShowRecentMessages,
        false,
    )

    SettingsPageContent(apiConfigured, baseApiUrlState, receiverKeyState, showRecentMessagesState)
}

@Composable
private fun SettingsPageContent(
    apiConfigured: Boolean,
    baseApiUrlState: MutableState<String>,
    receiverKeyState: MutableState<String>,
    showRecentMessagesState: MutableState<Boolean>,
) {
    AppContentSurface {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (!apiConfigured) {
                item { ApiSettingsWarningCard(Modifier.padding(AppSpacings.medium)) }
            }

            baseApiUrlPreference(baseApiUrlState)
            receiverKeyPreference(receiverKeyState)
            showRecentMessagesPreference(showRecentMessagesState)
        }
    }
}

@Preview
@Composable
private fun SettingsPageContentPreview(
) {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp)
    ) {
        AppPreferencesProvider {
            SettingsPageContent(
                apiConfigured = false,
                baseApiUrlState = remember { mutableStateOf("https://server.com") },
                receiverKeyState = remember { mutableStateOf("") },
                showRecentMessagesState = remember { mutableStateOf(true) },
            )
        }
    }
}
