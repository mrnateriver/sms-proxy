package io.mrnateriver.smsproxy.relay.layout

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptionsBuilder
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.pages.about.isAboutPageRoute
import io.mrnateriver.smsproxy.relay.pages.about.isLicensesPageRoute
import io.mrnateriver.smsproxy.relay.pages.about.navigateToAboutPage
import io.mrnateriver.smsproxy.relay.pages.about.navigateToLicensesPage
import io.mrnateriver.smsproxy.relay.pages.settings.isSettingsPageRoute
import io.mrnateriver.smsproxy.relay.pages.settings.navigateToSettingsPage

enum class AppPages(
    val icon: ImageVector? = null,
    @StringRes val titleResId: Int,
    val navigate: NavController.(builder: (NavOptionsBuilder.() -> Unit)?) -> Unit,
    val isActive: (dest: NavDestination?) -> Boolean,
    val popUpToRoot: Boolean = false,
) {
    SETTINGS(
        Icons.Outlined.Settings,
        R.string.page_title_settings,
        NavController::navigateToSettingsPage,
        ::isSettingsPageRoute,
        true,
    ),

    ABOUT(
        Icons.Outlined.Info,
        R.string.page_title_about,
        NavController::navigateToAboutPage,
        ::isAboutPageRoute,
        true,
    ),

    LICENSES(
        titleResId = R.string.page_title_licenses,
        navigate = NavController::navigateToLicensesPage,
        isActive = ::isLicensesPageRoute,
    );

    companion object {
        fun fromNavDestination(dest: NavDestination?): AppPages? {
            return entries.find { it.isActive(dest) }
        }
    }
}
