package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.composables.rememberMutableCoroutineState
import io.mrnateriver.smsproxy.relay.layout.AppContentSurface
import io.mrnateriver.smsproxy.shared.composables.AppPreferencesProvider
import io.mrnateriver.smsproxy.shared.composables.theme.AppSpacings
import io.mrnateriver.smsproxy.shared.pages.PageDescriptor
import io.mrnateriver.smsproxy.relay.services.usecases.contracts.SettingsService as SettingsServiceContract

fun NavGraphBuilder.settingsPage(
    onBackClick: () -> Unit = {},
    settingsService: SettingsServiceContract,
) {
    composable(SETTINGS_PAGE_ROUTE) {
        SettingsPage(settingsService, onBackClick)
    }
}

fun NavController.navigateToSettingsPage(builder: (NavOptionsBuilder.() -> Unit)? = null) {
    navigate(SETTINGS_PAGE_ROUTE, if (builder == null) null else navOptions(builder))
}

val settingsPageDescriptor = PageDescriptor(
    Icons.Outlined.Settings,
    R.string.page_title_settings,
    NavController::navigateToSettingsPage,
    ::isSettingsPageRoute,
    true,
)

private const val SETTINGS_PAGE_ROUTE = "settings"

private fun isSettingsPageRoute(dest: NavDestination?): Boolean = dest?.route == SETTINGS_PAGE_ROUTE

@Composable
fun SettingsPage(settingsService: SettingsServiceContract, onBackClick: () -> Unit = {}) {
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
        "",
    )
    val showRecentMessagesState = rememberMutableCoroutineState(
        settingsService.showRecentMessages,
        settingsService::setShowRecentMessages,
        false,
    )

    SettingsPageContent(
        apiConfigured,
        baseApiUrlState.value,
        receiverKeyState.value,
        showRecentMessagesState.value,
        onBaseApiUrlChange = { baseApiUrlState.value = it },
        onReceiverKeyChange = { receiverKeyState.value = it },
        onShowRecentMessagesChange = { showRecentMessagesState.value = it },
    )
}

@Composable
private fun SettingsPageContent(
    apiConfigured: Boolean,
    baseApiUrl: String,
    receiverKey: String,
    showRecentMessages: Boolean,
    onBaseApiUrlChange: (String) -> Unit = {},
    onReceiverKeyChange: (String) -> Unit = {},
    onShowRecentMessagesChange: (Boolean) -> Unit = {},
) {
    AppContentSurface {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (!apiConfigured) {
                item { ApiSettingsWarningCard(Modifier.padding(AppSpacings.medium)) }
            }

            baseApiUrlPreference(baseApiUrl, onBaseApiUrlChange)
            receiverKeyPreference(receiverKey, onReceiverKeyChange)
            showRecentMessagesPreference(showRecentMessages, onShowRecentMessagesChange)
        }
    }
}

@Preview
@Composable
private fun SettingsPageContentPreview() {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp),
    ) {
        AppPreferencesProvider {
            SettingsPageContent(
                apiConfigured = false,
                baseApiUrl = "https://server.com",
                receiverKey = "",
                showRecentMessages = true,
            )
        }
    }
}
