package io.mrnateriver.smsproxy.shared.composables

import androidx.compose.runtime.Composable
import io.mrnateriver.smsproxy.shared.composables.theme.appPreferencesTheme
import me.zhanghai.compose.preference.ProvidePreferenceLocals

@Composable
fun AppPreferencesProvider(content: @Composable () -> Unit) {
    ProvidePreferenceLocals(theme = appPreferencesTheme(), content = content)
}
