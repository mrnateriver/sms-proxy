package io.mrnateriver.smsproxy.shared.pages

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptionsBuilder

data class PageDescriptor(
    val icon: ImageVector? = null,
    @StringRes val titleResId: Int,
    val navigate: NavController.(builder: (NavOptionsBuilder.() -> Unit)?) -> Unit,
    val isActive: (dest: NavDestination?) -> Boolean,
    val popUpToRoot: Boolean = false,
)