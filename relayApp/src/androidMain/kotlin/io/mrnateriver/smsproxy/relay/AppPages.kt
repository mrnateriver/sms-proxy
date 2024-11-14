package io.mrnateriver.smsproxy.relay

import androidx.navigation.NavDestination
import io.mrnateriver.smsproxy.relay.pages.settings.settingsPageDescriptor
import io.mrnateriver.smsproxy.shared.pages.PageDescriptor
import io.mrnateriver.smsproxy.shared.pages.about.aboutPageDescriptor
import io.mrnateriver.smsproxy.shared.pages.about.licensesPageDescriptor

enum class AppPages(val descriptor: PageDescriptor) {
    SETTINGS(settingsPageDescriptor),
    ABOUT(aboutPageDescriptor),
    LICENSES(licensesPageDescriptor),
    ;

    companion object {
        fun fromNavDestination(dest: NavDestination?): AppPages? {
            return entries.find { it.descriptor.isActive(dest) }
        }
    }
}
