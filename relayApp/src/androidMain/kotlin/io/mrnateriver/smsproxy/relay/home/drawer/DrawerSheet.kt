package io.mrnateriver.smsproxy.relay.home.drawer

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.shared.AppSpacings

@Preview
@Composable
fun DrawerSheet(modifier: Modifier = Modifier) {
    ModalDrawerSheet(modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)) {
        Text(
            "SMS Relay", // TODO: i18n (also take the app's name from manifest or something)
            modifier = Modifier.padding(AppSpacings.large),
            style = MaterialTheme.typography.headlineSmall,
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(horizontal = AppSpacings.small),
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "" // TODO: label + i18n
                )
            },
            label = { Text("Settings") }, // TODO: i18n
            selected = false,
            onClick = {
                // TODO: navigate to settings
            },
        )
        NavigationDrawerItem(
            modifier = Modifier.padding(horizontal = AppSpacings.small),
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "", // TODO: label + i18n
                )
            },
            label = { Text("About") }, // TODO: i18n
            selected = false,
            onClick = {
                // TODO: navigate to settings
            },
        )
    }
}