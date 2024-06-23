package io.mrnateriver.smsproxy.relay

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptionsBuilder
import io.mrnateriver.smsproxy.relay.about.isAboutPageRoute
import io.mrnateriver.smsproxy.relay.about.isLicensesPageRoute
import io.mrnateriver.smsproxy.relay.about.navigateToAboutPage
import io.mrnateriver.smsproxy.relay.about.navigateToLicensesPage
import io.mrnateriver.smsproxy.relay.settings.isSettingsPageRoute
import io.mrnateriver.smsproxy.relay.settings.navigateToSettingsPage

enum class AppPages(
    val icon: ImageVector? = null,
    val title: String,
    val navigate: NavController.(builder: (NavOptionsBuilder.() -> Unit)?) -> Unit,
    val isActive: (dest: NavDestination?) -> Boolean,
    val popUpToRoot: Boolean = false,
) {
    SETTINGS(
        Icons.Outlined.Settings,
        "Settings",
        NavController::navigateToSettingsPage,
        ::isSettingsPageRoute,
        true,
    ),

    ABOUT(
        Icons.Outlined.Info,
        "About",
        NavController::navigateToAboutPage,
        ::isAboutPageRoute,
        true,
    ),

    LICENSES(
        title = "Licenses",
        navigate = NavController::navigateToLicensesPage,
        isActive = ::isLicensesPageRoute,
    );

    companion object {
        fun fromNavDestination(dest: NavDestination?): AppPages? {
            return entries.find { it.isActive(dest) }
        }
    }
}
