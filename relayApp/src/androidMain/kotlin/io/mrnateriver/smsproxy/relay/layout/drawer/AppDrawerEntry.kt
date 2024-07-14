package io.mrnateriver.smsproxy.relay.layout.drawer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.shared.theme.AppSpacings

@Preview
@Composable
fun AppDrawerEntry(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    label: String = "About",
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    NavigationDrawerItem(
        modifier = Modifier
            .padding(horizontal = AppSpacings.small)
            .padding(bottom = AppSpacings.tiny),
        icon = if (icon != null) {
            { Icon(imageVector = icon, contentDescription = null) }
        } else null,
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
    )
}
