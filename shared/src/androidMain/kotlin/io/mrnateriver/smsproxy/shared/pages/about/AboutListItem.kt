package io.mrnateriver.smsproxy.shared.pages.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AboutListItem(
    modifier: Modifier = Modifier,
    image: (@Composable () -> Unit)? = null,
    text: String,
    title: String,
    onClick: (() -> Unit)? = null,
) {
    ListItem(
        leadingContent = { Box(modifier = Modifier.size(48.dp)) { image?.invoke() } },
        overlineContent = { Text(title, style = MaterialTheme.typography.titleMedium) },
        headlineContent = { Text(text, style = MaterialTheme.typography.bodyMedium) },
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        trailingContent = if (onClick != null) {
            {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        } else null
    )
}

@Preview
@Composable
fun AboutListItemPreview() {
    AboutListItem(
        image = {
            Icon(
                imageVector = Icons.Outlined.Face,
                contentDescription = "Face Icon",
                modifier = Modifier.fillMaxSize()
            )
        },
        text = "List Item Text",
        title = "List Item Title",
    )
}