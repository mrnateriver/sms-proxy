package io.mrnateriver.smsproxy.relay

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
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
        content = content,
    )
}