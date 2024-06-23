package io.mrnateriver.smsproxy.relay.about

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import androidx.navigation.navigation
import coil.compose.AsyncImage
import io.mrnateriver.smsproxy.relay.AppContentSurface

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
            AboutListItem(
                text = "https://mrnateriver.io",
                title = "Author",
                image = {
                    AsyncImage(
                        model = "https://avatars.githubusercontent.com/u/22825372?v=4",
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(48.dp)
                    )
                },
                onClick = {
                    Log.d("AboutPage", "Author clicked")
                }
            )
            HorizontalDivider()

            AboutListItem(
                title = "Open Source Licenses",
                text = "Third-party software licenses",
                onClick = navigateToLicensesPage,
            )
            HorizontalDivider()

            AboutListItem(title = "Version", text = "1.0.0")
        }
    }
}

@Composable
private fun AboutListItem(
    modifier: Modifier = Modifier,
    image: (@Composable () -> Unit)? = null,
    text: String,
    title: String,
    onClick: (() -> Unit)? = null,
) {
    ListItem(
        leadingContent = { Box(modifier = Modifier.size(48.dp)) { image?.invoke() } },
        overlineContent = { Text(title, style = MaterialTheme.typography.titleMedium) },
        headlineContent = { Text(text, style = MaterialTheme.typography.bodyLarge) },
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        trailingContent = if (onClick != null) {
            {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        } else null
    )
}