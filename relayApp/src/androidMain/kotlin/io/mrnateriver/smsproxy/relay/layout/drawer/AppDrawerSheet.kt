package io.mrnateriver.smsproxy.relay.layout.drawer

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.shared.theme.AppSpacings

@Preview
@Composable
fun AppDrawerSheet(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    ModalDrawerSheet(modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)) {
        Text(
            stringResource(R.string.app_name),
            modifier = Modifier.padding(AppSpacings.large),
            style = MaterialTheme.typography.headlineSmall,
        )

        content()
    }
}
