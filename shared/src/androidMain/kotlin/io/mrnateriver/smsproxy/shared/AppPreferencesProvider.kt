package io.mrnateriver.smsproxy.shared

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.mrnateriver.smsproxy.shared.theme.appPreferencesTheme
import me.zhanghai.compose.preference.ProvidePreferenceLocals

// TODO: move to shared, could be reused in receiverApp
@Composable
fun AppPreferencesProvider(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    ProvidePreferenceLocals(theme = appPreferencesTheme(), content = content)
}