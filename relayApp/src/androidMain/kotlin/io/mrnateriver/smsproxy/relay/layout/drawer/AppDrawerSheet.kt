package io.mrnateriver.smsproxy.relay.layout.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.mrnateriver.smsproxy.relay.R
import io.mrnateriver.smsproxy.shared.composables.theme.AppSpacings

@Composable
fun AppDrawerSheet(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    ModalDrawerSheet(modifier = modifier.windowInsetsPadding(WindowInsets.systemBars)) {
        Text(
            stringResource(R.string.app_name),
            modifier = Modifier.padding(AppSpacings.large),
            style = MaterialTheme.typography.headlineSmall,
        )

        content()
    }
}

@Preview
@Composable
private fun AppDrawerSheetPreview() {
    Box(
        Modifier
            .background(Color.Black)
            .padding(16.dp)
    ) {
        AppDrawerSheet {
            for (i in 0..10) {
                Box(
                    Modifier
                        .background(Color.Cyan.copy(alpha = 0.2f))
                        .padding(16.dp)
                        .fillMaxWidth()
                ) { Text("Content $i") }

                if (i < 10) {
                    HorizontalDivider()
                }
            }
        }
    }
}