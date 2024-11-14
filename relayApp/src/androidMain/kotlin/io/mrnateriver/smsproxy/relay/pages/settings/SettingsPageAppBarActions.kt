package io.mrnateriver.smsproxy.relay.pages.settings

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
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.relay.layout.appbar.AppBarActions

@Composable
fun SettingsPageAppBarActions(onBackClick: () -> Unit = {}) {
    AppBarActions {
        SettingsPageAppBarSaveAction(onBackClick)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SettingsPageAppBarSaveAction(onBackClick: () -> Unit = {}) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        state = rememberTooltipState(),
        tooltip = { PlainTooltip { Text(stringResource(R.string.settings_page_app_bar_action_save_tooltip)) } },
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = stringResource(R.string.settings_page_app_bar_action_save_label),
            )
        }
    }
}

@Preview
@Composable
private fun SettingsPageAppBarActionsPreview() {
    SettingsPageAppBarSaveAction()
}
