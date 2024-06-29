package io.mrnateriver.smsproxy.relay.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.layout.appbar.AppBar
import io.mrnateriver.smsproxy.relay.layout.appbar.rememberRootBackgroundColor
import io.mrnateriver.smsproxy.relay.layout.drawer.AppDrawer

@Preview
@Composable
fun AppLayout(
    modifier: Modifier = Modifier,
    isHomePage: Boolean = true,
    title: String? = null,
    onNavigateUpClicked: () -> Unit = {},
    drawerContent: @Composable (toggleDrawer: () -> Unit) -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    AppDrawer(
        modifier = modifier,
        drawerContent = drawerContent,
    ) { fraction, toggleDrawer ->
        val buttonClick: () -> Unit = {
            if (isHomePage) {
                toggleDrawer()
            } else {
                onNavigateUpClicked()
            }
        }

        val containerColor = rememberRootBackgroundColor()
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = containerColor,
            topBar = {
                AppBar(
                    title = title,
                    navigationIconImageVector = if (isHomePage) Icons.Outlined.Menu else Icons.AutoMirrored.Outlined.ArrowBack,
                    onNavigationButtonClick = buttonClick,
                    navigationIconContentDescription =
                    if (isHomePage) stringResource(R.string.app_bar_navigation_button_label_drawer)
                    else stringResource(R.string.app_bar_navigation_button_label_back),
                )
            },
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 1 - 0.05f * fraction
                    scaleY = 1 - 0.05f * fraction
                    transformOrigin = TransformOrigin(0.5f, 1f)
                }
                .padding(it)) {
                content()
            }
        }
    }
}
