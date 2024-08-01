package io.mrnateriver.smsproxy.relay.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.mrnateriver.smsproxy.relay.layout.drawer.HandleAppDrawerBackButton

@Composable
fun AppContentSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge.copy(
            bottomStart = ZeroCornerSize,
            bottomEnd = ZeroCornerSize,
        ),
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize(),
        content = {
            content()
            HandleAppDrawerBackButton()
        },
    )
}

@Preview
@Composable
private fun AppContentSurfacePreview() {
    Box(
        Modifier
            .background(Color.Black)
            .padding(16.dp)
    ) {
        AppContentSurface()
    }
}