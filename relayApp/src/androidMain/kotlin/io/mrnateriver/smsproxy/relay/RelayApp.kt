package io.mrnateriver.smsproxy.relay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.mrnateriver.smsproxy.shared.AppMaterialTheme
import io.mrnateriver.smsproxy.shared.AppSpacings

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun RelayApp() {
    val receiveSmsPermissionResult = rememberSmsPermissionState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            ModalDrawerSheet { /* Drawer content */ }
//        },
//    ) {
//        Scaffold(
//            floatingActionButton = {
//                ExtendedFloatingActionButton(
//                    text = { Text("Show drawer") },
//                    icon = { Icon(Icons.Filled.Add, contentDescription = "") },
//                    onClick = {
//                        scope.launch {
//                            drawerState.apply {
//                                if (isClosed) open() else close()
//                            }
//                        }
//                    }
//                )
//            }
//        ) { contentPadding ->
//            // Screen content
//        }
//    }

    AppMaterialTheme {
        ModalNavigationDrawer(drawerContent = {
            ModalDrawerSheet {
                Text("Drawer title", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(label = { Text(text = "Drawer Item") },
                    selected = false,
                    onClick = { /*TODO*/ })
                // ...other drawer items
            }
        }) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            scrolledContainerColor = MaterialTheme.colorScheme.primary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        navigationIcon = {
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(imageVector = Icons.Outlined.Menu, contentDescription = "")
                            }
                        }, title = { Text(text = "SMS Proxy Relay") })
                },
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge.copy(
                        bottomStart = ZeroCornerSize, bottomEnd = ZeroCornerSize
                    ),
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                ) {
                    // TODO: lazy column because cards might not fit on the screen
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(AppSpacings.medium),
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