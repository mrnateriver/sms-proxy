package io.mrnateriver.smsproxy.shared.pages.about

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import io.mrnateriver.smsproxy.shared.R
import io.mrnateriver.smsproxy.shared.pages.PageDescriptor

fun NavGraphBuilder.licensesPage(
    pageContentWrapper: @Composable (content: @Composable () -> Unit) -> Unit = { it() },
) {
    composable(LICENSES_PAGE_ROUTE) {
        LicensesPage(pageContentWrapper = pageContentWrapper)
    }
}

fun NavController.navigateToLicensesPage(builder: (NavOptionsBuilder.() -> Unit)? = null) {
    navigate(
        LICENSES_PAGE_ROUTE,
        if (builder == null) null else navOptions(builder),
    )
}

val licensesPageDescriptor = PageDescriptor(
    titleResId = R.string.page_title_licenses,
    navigate = NavController::navigateToLicensesPage,
    isActive = ::isLicensesPageRoute,
)

private const val LICENSES_PAGE_ROUTE = "licenses"

private fun isLicensesPageRoute(dest: NavDestination?): Boolean = dest?.route == LICENSES_PAGE_ROUTE

@Composable
fun LicensesPage(
    pageContentWrapper: @Composable (content: @Composable () -> Unit) -> Unit = { it() },
) {
    pageContentWrapper {
        LibrariesContainer()
    }
}
