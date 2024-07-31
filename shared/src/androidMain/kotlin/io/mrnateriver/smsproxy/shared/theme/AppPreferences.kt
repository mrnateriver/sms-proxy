package io.mrnateriver.smsproxy.shared.theme

import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import me.zhanghai.compose.preference.PreferenceTheme
import me.zhanghai.compose.preference.preferenceTheme

@Composable
fun appPreferencesTheme(): PreferenceTheme {
    return preferenceTheme(
        summaryTextStyle = MaterialTheme.typography.bodySmall,
        titleTextStyle = MaterialTheme.typography.titleSmall.copy(
            color = ListItemDefaults.colors().overlineColor, // To match "About" page styles
        ),
    )
}