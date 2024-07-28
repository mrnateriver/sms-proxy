package io.mrnateriver.smsproxy.relay.pages.about

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.navigation
import io.mrnateriver.smsproxy.relay.layout.AppContentSurface

private const val AboutPageRoot = "about"
private const val AboutPageInfo = "info"

fun NavGraphBuilder.aboutPage(navController: NavController) {
    navigation(route = AboutPageRoot, startDestination = AboutPageInfo) {
        composable(AboutPageInfo) {
            AboutPage(navigateToLicensesPage = { navController.navigateToLicensesPage() })
        }

        licensesPage()
    }
}

fun NavController.navigateToAboutPage(builder: (NavOptionsBuilder.() -> Unit)? = null) {
    navigate(AboutPageRoot, if (builder == null) null else navOptions(builder))
}

fun isAboutPageRoute(dest: NavDestination?): Boolean = dest?.route == AboutPageInfo

// FIXME: THIS CAN BE SHARED FOR BOTH RELAY/RECEIVER APP!

@Preview
@Composable
fun AboutPage(navigateToLicensesPage: () -> Unit = {}) {
    AppContentSurface {
        Column {
            AboutAuthorItem()
            AboutLicensesItem(navigateToLicensesPage = navigateToLicensesPage)
            AboutVersionItem()
        }
    }
}