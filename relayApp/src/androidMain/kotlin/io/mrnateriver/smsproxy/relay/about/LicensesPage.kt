package io.mrnateriver.smsproxy.relay.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import io.mrnateriver.smsproxy.relay.layout.AppContentSurface

private const val LicensesPageRoute = "licenses"

fun NavGraphBuilder.licensesPage() {
    composable(LicensesPageRoute) {
        LicensesPage()
    }
}

fun NavController.navigateToLicensesPage(builder: (NavOptionsBuilder.() -> Unit)? = null) {
    navigate(LicensesPageRoute, if (builder == null) null else navOptions(builder))
}

fun isLicensesPageRoute(dest: NavDestination?): Boolean = dest?.route == LicensesPageRoute

@Preview
@Composable
fun LicensesPage() {
    AppContentSurface {
        LibrariesContainer()
    }
}
