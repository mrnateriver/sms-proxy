package io.mrnateriver.smsproxy.relay.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.home.dashboard.Dashboard
import io.mrnateriver.smsproxy.relay.home.dashboard.DashboardSurface
import io.mrnateriver.smsproxy.relay.home.drawer.DrawerWithAnimation

@Preview
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    DrawerWithAnimation(modifier = modifier) { fraction, toggleDrawer ->
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = MaterialTheme.colorScheme.primary,
            topBar = { AppBar(onMenuButtonClick = toggleDrawer) },
        ) {
            DashboardSurface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                fraction = fraction,
            ) {
                Dashboard(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .windowInsetsPadding(WindowInsets.navigationBars),
                )
            }
        }
    }
}