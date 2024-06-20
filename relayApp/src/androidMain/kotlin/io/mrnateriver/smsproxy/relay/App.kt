package io.mrnateriver.smsproxy.relay

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.home.HomePage
import io.mrnateriver.smsproxy.shared.AppMaterialTheme

@Preview
@Composable
fun App() {
    AppMaterialTheme {
        HomePage()
    }
}