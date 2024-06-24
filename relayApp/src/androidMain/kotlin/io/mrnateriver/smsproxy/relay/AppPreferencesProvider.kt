package io.mrnateriver.smsproxy.relay

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preferenceTheme

@Composable
fun AppPreferencesProvider(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    ProvidePreferenceLocals(
        theme = preferenceTheme(
            titleTextStyle = MaterialTheme.typography.titleMedium.copy(
                color = ListItemDefaults.colors().overlineColor, // To match "About" page styles
            ),
        ),
        content = content,
    )
}