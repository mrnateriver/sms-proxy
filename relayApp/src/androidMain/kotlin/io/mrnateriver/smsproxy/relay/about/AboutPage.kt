package io.mrnateriver.smsproxy.relay.about

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions

private val AboutPageRoute = "about"

fun NavGraphBuilder.aboutPage() {
    composable(AboutPageRoute) {
        AboutPage()
    }
}

fun NavController.navigateToAboutPage(builder: (NavOptionsBuilder.() -> Unit)? = null) {
    navigate(AboutPageRoute, if (builder == null) null else navOptions(builder))
}

fun isAboutPageRoute(dest: NavDestination?): Boolean = dest?.route == AboutPageRoute

@Preview
@Composable
fun AboutPage() {
    Text(text = "About Page") // TODO: everything
}
