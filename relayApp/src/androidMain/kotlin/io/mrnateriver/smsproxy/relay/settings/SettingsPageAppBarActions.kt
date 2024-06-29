package io.mrnateriver.smsproxy.relay.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import io.mrnateriver.smsproxy.relay.layout.appbar.AppBarActions

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsPageAppBarActions(navController: NavController) {
    AppBarActions {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            state = rememberTooltipState(),
            tooltip = {
                PlainTooltip {
                    Text("Save settings") // TODO: i18n
                }
            },
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Save Settings", // TODO: i18n
                )
            }
        }
    }
}