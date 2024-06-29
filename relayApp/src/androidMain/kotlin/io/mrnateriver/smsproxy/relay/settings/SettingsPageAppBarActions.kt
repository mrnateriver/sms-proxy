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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.layout.appbar.AppBarActions

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SettingsPageAppBarActions(navController: NavController) {
    AppBarActions {
        TooltipBox(
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            state = rememberTooltipState(),
            tooltip = { PlainTooltip { Text(stringResource(R.string.settings_page_app_bar_action_save_tooltip)) } },
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = stringResource(R.string.settings_page_app_bar_action_save_label),
                )
            }
        }
    }
}