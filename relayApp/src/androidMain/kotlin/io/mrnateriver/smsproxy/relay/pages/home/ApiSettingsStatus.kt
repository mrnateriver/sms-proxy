package io.mrnateriver.smsproxy.relay.pages.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.shared.theme.AppSpacings

@Preview
@Composable
fun ApiSettingsStatus(modifier: Modifier = Modifier, onGoToSettingsClick: () -> Unit = {}) {
    // TODO: refactor into a reusable card along with ApiSettingsWarningCard
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier.padding(AppSpacings.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacings.medium),
        ) {
            Text(
                text = stringResource(R.string.home_page_api_settings_card_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(text = stringResource(R.string.home_page_api_settings_card_text))
            Button(onClick = onGoToSettingsClick) {
                Text(text = stringResource(R.string.home_page_api_settings_card_button_label))
            }
        }
    }
}