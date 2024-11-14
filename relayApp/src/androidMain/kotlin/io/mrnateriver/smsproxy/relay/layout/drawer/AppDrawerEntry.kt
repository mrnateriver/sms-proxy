package io.mrnateriver.smsproxy.relay.layout.drawer

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.shared.composables.theme.AppSpacings

@Composable
fun AppDrawerEntry(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconContentDescription: String? = null,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    NavigationDrawerItem(
        modifier = modifier
            .padding(horizontal = AppSpacings.small)
            .padding(bottom = AppSpacings.tiny),
        icon = if (icon != null) {
            { Icon(imageVector = icon, contentDescription = iconContentDescription) }
        } else {
            null
        },
        label = { Text(label, style = MaterialTheme.typography.bodyLarge) },
        selected = selected,
        onClick = onClick,
    )
}

@Preview("selected == false")
@Composable
private fun AppDrawerEntryPreview_NotSelected() {
    AppDrawerEntry(icon = Icons.Rounded.AccountCircle, label = "Drawer Entry", selected = false)
}

@Preview("selected == true")
@Composable
private fun AppDrawerEntryPreview_Selected() {
    AppDrawerEntry(icon = Icons.Rounded.AccountCircle, label = "Drawer Entry", selected = true)
}

@Preview("no icon")
@Composable
private fun AppDrawerEntryPreview_NoIcon() {
    AppDrawerEntry(label = "Drawer Entry", selected = false)
}
