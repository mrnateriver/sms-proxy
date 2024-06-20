package io.mrnateriver.smsproxy.relay.home.dashboard

import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun DashboardSurface(
    modifier: Modifier = Modifier,
    fraction: Float = 0f,
    content: @Composable () -> Unit = {},
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge.copy(
            bottomStart = ZeroCornerSize,
            bottomEnd = ZeroCornerSize,
        ),
        color = MaterialTheme.colorScheme.background,
        modifier = modifier
            .graphicsLayer {
                scaleX = 1 - 0.05f * fraction
                scaleY = 1 - 0.05f * fraction
                transformOrigin = TransformOrigin(0.5f, 1f)
            },
        content = content,
    )
}