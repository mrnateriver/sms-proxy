package io.mrnateriver.smsproxy.shared.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.mrnateriver.smsproxy.shared.composables.theme.AppSpacings

enum class AlertMessageType {
    INFO,
    ERROR,
}

data class AlertMessageAction(
    val label: String,
    val action: () -> Unit,
)

@Composable
fun AlertMessage(
    text: String,
    modifier: Modifier = Modifier,
    type: AlertMessageType = AlertMessageType.INFO,
    textIconVector: ImageVector? = null,
    textIconContentDescription: String? = null,
    title: String? = null,
    action: AlertMessageAction? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        contentColor = MaterialTheme.colorScheme.onSurface,
        color = when (type) {
            AlertMessageType.ERROR -> MaterialTheme.colorScheme.errorContainer
            else -> MaterialTheme.colorScheme.secondaryContainer
        },
    ) {
        Column(
            modifier = Modifier.padding(AppSpacings.medium),
            verticalArrangement = Arrangement.spacedBy(AppSpacings.small),
        ) {
            if (title != null) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
            }

            if (textIconVector != null) {
                Row(modifier = Modifier.padding()) {
                    var offsetText by remember { mutableStateOf(false) }

                    Icon(
                        imageVector = textIconVector,
                        contentDescription = textIconContentDescription,
                    )
                    Spacer(modifier = Modifier.width(AppSpacings.small))
                    Text(
                        text = text,
                        modifier = Modifier.padding(top = if (offsetText) AppSpacings.small / 2 else 0.dp),
                        onTextLayout = { layoutResult -> offsetText = layoutResult.lineCount == 1 },
                    )
                }
            } else {
                Text(text = text)
            }

            if (action != null) {
                Button(onClick = action.action) { Text(text = action.label) }
            }
        }
    }
}

@Preview
@Composable
private fun AlertMessage_Info() {
    AlertMessage(
        type = AlertMessageType.INFO,
        text = "This is an info message",
    )
}

@Preview
@Composable
private fun AlertMessage_Info_TextIcon_SingleLine() {
    AlertMessage(
        type = AlertMessageType.INFO,
        text = "This is an info message",
        textIconVector = Icons.Outlined.Info,
    )
}

@Preview
@Composable
private fun AlertMessage_Info_TextIcon() {
    AlertMessage(
        type = AlertMessageType.INFO,
        text = "This is an info message with a long text so that it spans multiple lines.".repeat(n = 32),
        textIconVector = Icons.Outlined.Info,
    )
}

@Preview
@Composable
private fun AlertMessage_Info_WithTitleAndButton() {
    AlertMessage(
        type = AlertMessageType.INFO,
        text = "This is an info message",
        title = "Alert Message",
        action = AlertMessageAction("Do Something") {},
    )
}

@Preview
@Composable
private fun AlertMessage_Info_WithTitleAndButton_TextIcon() {
    AlertMessage(
        type = AlertMessageType.INFO,
        text = "This is an info message",
        textIconVector = Icons.Outlined.Face,
        title = "Alert Message",
        action = AlertMessageAction("Do Something") {},
    )
}

@Preview
@Composable
private fun AlertMessage_Error_WithTitleAndButton_TextIcon() {
    AlertMessage(
        type = AlertMessageType.ERROR,
        text = "This is an info message with a long text so that it spans multiple lines.".repeat(n = 32),
        textIconVector = Icons.Outlined.Warning,
        title = "Alert Message",
        action = AlertMessageAction("Do Something") {},
    )
}
