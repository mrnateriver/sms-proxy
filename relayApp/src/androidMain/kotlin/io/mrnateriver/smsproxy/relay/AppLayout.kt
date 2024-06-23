package io.mrnateriver.smsproxy.relay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.drawer.AppDrawer

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

        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = MaterialTheme.colorScheme.primary,
            topBar = {
                AppBar(
                    title = title,
                    isHomePage = isHomePage,
                    onMenuButtonClick = buttonClick,
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
