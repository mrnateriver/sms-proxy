package io.mrnateriver.smsproxy.relay.layout.drawer

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    drawerContent: @Composable (toggleDrawer: () -> Unit) -> Unit = {},
    content: (@Composable (progress: Float, toggleDrawer: () -> Unit) -> Unit) = { _, _ -> },
) {
    val scope = rememberCoroutineScope()

    val toggleDrawerState: () -> Unit = remember {
        {
            scope.launch {
                drawerState.apply {
                    if (isClosed) open() else close()
                }
            }
        }
    }

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = { AppDrawerSheet(content = { drawerContent(toggleDrawerState) }) },
    ) {
        val density = LocalDensity.current
        val fraction =
            1f + (drawerState.currentOffset / density.density) / DrawerDefaults.MaximumDrawerWidth.value

        CompositionLocalProvider(
            LocalAppDrawerState provides drawerState,
            content = {
                content(
                    if (fraction.isNaN()) 0f else fraction,
                    toggleDrawerState,
                )
            },
        )
    }
}

val LocalAppDrawerState =
    compositionLocalOf<DrawerState> { error("CompositionLocal LocalAppDrawerState not present") }

@Composable
fun HandleAppDrawerBackButton() {
    val drawerState = LocalAppDrawerState.current
    val scope = rememberCoroutineScope()

    BackHandler(
        enabled = drawerState.isOpen,
        onBack = {
            scope.launch {
                drawerState.close()
            }
        },
    )
}

@Preview(heightDp = 350)
@Composable
private fun AppDrawerPreview_DrawerOpen() {
    Box(
        Modifier
            .background(Color.Black)
            .padding(16.dp)
    ) {
        AppDrawer(
            drawerContent = {
                Box(
                    Modifier
                        .background(Color.Cyan.copy(alpha = 0.2f))
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Drawer Content", color = Color.Black)
                }
            },
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
        ) { fraction, _ ->
            Box(
                Modifier
                    .background(Color.Red.copy(alpha = 0.5f))
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Main Content: $fraction", color = Color.White)
            }
        }
    }
}

@Preview(heightDp = 350)
@Composable
private fun AppDrawerPreview_DrawerClosed() {
    Box(
        Modifier
            .background(Color.Black)
            .padding(16.dp)
    ) {
        AppDrawer(
            drawerContent = {
                Box(
                    Modifier
                        .background(Color.Cyan.copy(alpha = 0.2f))
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Drawer Content", color = Color.Black)
                }
            },
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
        ) { fraction, _ ->
            Box(
                Modifier
                    .background(Color.Red.copy(alpha = 0.5f))
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Main Content: $fraction", color = Color.White)
            }
        }
    }
}
