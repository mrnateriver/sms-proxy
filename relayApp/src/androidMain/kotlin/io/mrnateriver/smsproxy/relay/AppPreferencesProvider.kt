package io.mrnateriver.smsproxy.relay

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preferenceTheme

// TODO: move to shared, could be reused in receiverApp
@Composable
fun AppPreferencesProvider(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    ProvidePreferenceLocals(
        // TODO: theme, regardless of what it pertains to, should be put alongside MD3 theme
        theme = preferenceTheme(
            titleTextStyle = MaterialTheme.typography.titleMedium.copy(
                color = ListItemDefaults.colors().overlineColor, // To match "About" page styles
            ),
        ),
        content = content,
    )
}