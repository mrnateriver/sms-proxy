package io.mrnateriver.smsproxy.relay.drawer

import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

@Preview
@Composable
fun AppDrawer(
    modifier: Modifier = Modifier,
    drawerContent: @Composable (toggleDrawer: () -> Unit) -> Unit = {},
    content: (@Composable (progress: Float, toggleDrawer: () -> Unit) -> Unit) = { _, _ -> },
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
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

        content(
            if (fraction.isNaN()) 0f else fraction,
            toggleDrawerState,
        )
    }
}