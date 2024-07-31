package io.mrnateriver.smsproxy.shared.pages.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.navigation
import io.mrnateriver.smsproxy.shared.R
import io.mrnateriver.smsproxy.shared.pages.PageDescriptor

fun NavGraphBuilder.aboutPage(
    navController: NavController,
    versionString: String? = null,
    pageContentWrapper: @Composable (content: @Composable () -> Unit) -> Unit = { it() },
) {
    navigation(
        route = AboutPageRoot,
        startDestination = AboutPageInfo
    ) {
        composable(AboutPageInfo) {
            AboutPage(
                versionString = versionString,
                navigateToLicensesPage = { navController.navigateToLicensesPage() },
                pageContentWrapper = pageContentWrapper,
            )
        }

        licensesPage(pageContentWrapper = pageContentWrapper)
    }
}

fun NavController.navigateToAboutPage(builder: (NavOptionsBuilder.() -> Unit)? = null) {
    navigate(
        AboutPageRoot,
        if (builder == null) null else navOptions(builder)
    )
}

val aboutPageDescriptor = PageDescriptor(
    Icons.Outlined.Info,
    R.string.page_title_about,
    NavController::navigateToAboutPage,
    ::isAboutPageRoute,
    true,
)

private const val AboutPageRoot = "about"
private const val AboutPageInfo = "info"

private fun isAboutPageRoute(dest: NavDestination?): Boolean = dest?.route == AboutPageInfo

@Composable
fun AboutPage(
    versionString: String? = null,
    navigateToLicensesPage: () -> Unit = {},
    pageContentWrapper: @Composable (content: @Composable () -> Unit) -> Unit = { it() },
) {
    pageContentWrapper {
        Column {
            AboutAuthorItem()
            AboutLicensesItem(navigateToLicensesPage = navigateToLicensesPage)
            AboutVersionItem(versionString = versionString)
        }
    }
}

@Preview
@Composable
private fun AboutPagePreview() {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp)
    ) {
        AboutPage(versionString = "1.0.0")
    }
}
