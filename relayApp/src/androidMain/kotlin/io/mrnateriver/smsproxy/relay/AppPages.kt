package io.mrnateriver.smsproxy.relay

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptionsBuilder
import io.mrnateriver.smsproxy.relay.about.isAboutPageRoute
import io.mrnateriver.smsproxy.relay.about.navigateToAboutPage
import io.mrnateriver.smsproxy.relay.settings.isSettingsPageRoute
import io.mrnateriver.smsproxy.relay.settings.navigateToSettingsPage

enum class AppPages(
    val icon: ImageVector,
    val title: String,
    val navigate: NavController.(builder: (NavOptionsBuilder.() -> Unit)?) -> Unit,
    val isActive: (dest: NavDestination?) -> Boolean,
) {
    ABOUT(
        Icons.Outlined.Info,
        "About",
        NavController::navigateToAboutPage,
        ::isAboutPageRoute,
    ),

    SETTINGS(
        Icons.Outlined.Settings,
        "Settings",
        NavController::navigateToSettingsPage,
        ::isSettingsPageRoute
    );

    companion object {
        fun fromNavDestination(dest: NavDestination?): AppPages? {
            return entries.find { it.isActive(dest) }
        }
    }
}
