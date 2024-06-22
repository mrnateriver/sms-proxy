package io.mrnateriver.smsproxy.relay.drawer

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.shared.AppSpacings

@Preview
@Composable
fun AppDrawerEntry(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Info,
    label: String = "About",
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    NavigationDrawerItem(
        modifier = Modifier.padding(horizontal = AppSpacings.small),
        icon = { Icon(imageVector = icon, contentDescription = null) },
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
    )
}
