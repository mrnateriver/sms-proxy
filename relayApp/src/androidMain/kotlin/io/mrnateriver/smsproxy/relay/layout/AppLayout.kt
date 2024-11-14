package io.mrnateriver.smsproxy.relay.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.layout.appbar.AppBar
import io.mrnateriver.smsproxy.relay.layout.drawer.AppDrawer
import io.mrnateriver.smsproxy.shared.composables.rememberRootBackgroundColor

private const val SCALE_DOWN_ANIMATION_THRESHOLD = 0.05f

@Composable
fun AppLayout(
    modifier: Modifier = Modifier,
    isHomePage: Boolean = true,
    title: String? = null,
    onNavigateUpClick: () -> Unit = {},
    drawerContent: @Composable ColumnScope.(toggleDrawer: () -> Unit) -> Unit = {},
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
                onNavigateUpClick()
            }
        }

        val containerColor = rememberRootBackgroundColor()
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = containerColor,
            topBar = {
                AppBar(
                    title = title,
                    navigationIconImageVector = if (isHomePage) {
                        Icons.Outlined.Menu
                    } else {
                        Icons.AutoMirrored.Outlined.ArrowBack
                    },
                    onNavigationButtonClick = buttonClick,
                    navigationIconContentDescription = if (isHomePage) {
                        stringResource(R.string.app_bar_navigation_button_label_drawer)
                    } else {
                        stringResource(R.string.app_bar_navigation_button_label_back)
                    },
                )
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = 1 - SCALE_DOWN_ANIMATION_THRESHOLD * fraction
                        scaleY = 1 - SCALE_DOWN_ANIMATION_THRESHOLD * fraction
                        transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 1f)
                    }
                    .padding(it),
            ) {
                content()
            }
        }
    }
}

@Preview
@Composable
private fun AppLayoutPreview() {
    AppLayout(
        title = "App Layout",
        drawerContent = {
            Box(
                Modifier
                    .background(Color.Cyan.copy(alpha = 0.2f))
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Text("Drawer Content", color = Color.Black)
            }
        },
    ) {
        Box(
            Modifier
                .background(Color.Red.copy(alpha = 0.5f))
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text("Main Content", color = Color.White)
        }
    }
}
