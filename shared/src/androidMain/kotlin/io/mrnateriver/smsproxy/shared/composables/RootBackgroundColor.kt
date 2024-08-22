package io.mrnateriver.smsproxy.shared.composables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun rememberRootBackgroundColor(): Color {
    return if (isSystemInDarkTheme()) {
        MaterialTheme.colorScheme.surfaceContainerLowest
    } else {
        MaterialTheme.colorScheme.primary
    }
}
