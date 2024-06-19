package io.mrnateriver.smsproxy.relay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.home.SmsLastRecord
import io.mrnateriver.smsproxy.relay.home.SmsPermissionsStatus
import io.mrnateriver.smsproxy.relay.home.SmsStats
import io.mrnateriver.smsproxy.relay.permissions.rememberSmsPermissions
import io.mrnateriver.smsproxy.shared.AppMaterialTheme
import io.mrnateriver.smsproxy.shared.AppSpacings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RelayApp() {
    val receiveSmsPermissionResult = rememberSmsPermissions()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val density = LocalDensity.current
    val fraction =
        (drawerState.currentOffset / density.density) / DrawerDefaults.MaximumDrawerWidth.value
    val scaleState = 1 - 0.05f * (1f + fraction)

    AppMaterialTheme {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)) {
                    Text(
                        "SMS Relay",
                        modifier = Modifier.padding(AppSpacings.large),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    NavigationDrawerItem(
                        modifier = Modifier.padding(horizontal = AppSpacings.small),
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = ""
                            )
                        },
                        label = { Text("Settings") },
                        selected = false,
                        onClick = {
                            // TODO: navigate to settings
                        }
                    )
                    NavigationDrawerItem(
                        modifier = Modifier.padding(horizontal = AppSpacings.small),
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = ""
                            )
                        },
                        label = { Text("About") },
                        selected = false,
                        onClick = {
                            // TODO: navigate to settings
                        }
                    )
                }
            }) {
            Scaffold(
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                containerColor = MaterialTheme.colorScheme.primary,
                topBar = {
                    TopAppBar(colors = TopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        scrolledContainerColor = MaterialTheme.colorScheme.primary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ), navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = "" // TODO: desc
                            )
                        }
                    }, title = { Text(text = "SMS Relay") })
                },
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge.copy(
                        bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize
                    ),
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .graphicsLayer {
                            scaleX = scaleState
                            scaleY = scaleState
                            // Adjust the pivot to zoom from the center
                            transformOrigin = TransformOrigin(0.5f, 1f)
                        },
                ) {
                    // TODO: lazy column because cards might not fit on the screen
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(AppSpacings.medium)
                            .windowInsetsPadding(WindowInsets.navigationBars),
                        verticalArrangement = Arrangement.spacedBy(AppSpacings.medium)
                    ) {
                        SmsPermissionsStatus(
                            modifier = Modifier.fillMaxWidth(),
                            permissionState = receiveSmsPermissionResult
                        )

                        SmsStats()

                        SmsLastRecord()
                    }
                }
            }
        }
    }
}