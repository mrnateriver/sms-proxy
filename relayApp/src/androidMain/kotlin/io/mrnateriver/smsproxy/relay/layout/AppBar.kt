package io.mrnateriver.smsproxy.relay.layout

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R

@Composable
fun rememberRootBackgroundColor(): Color {
    return if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.primary
}

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    isHomePage: Boolean = true,
    onMenuButtonClick: () -> Unit = {},
) {
    val contentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
    val containerColor = rememberRootBackgroundColor()

    val colors = TopAppBarColors(
        containerColor = containerColor,
        scrolledContainerColor = containerColor,
        actionIconContentColor = contentColor,
        titleContentColor = contentColor,
        navigationIconContentColor = contentColor,
    )

    TopAppBar(
        colors = colors,
        navigationIcon = {
            NavigationDrawerButton(
                imageVector = if (isHomePage) Icons.Outlined.Menu else Icons.AutoMirrored.Outlined.ArrowBack,
                onMenuButtonClick = onMenuButtonClick,
            )
        },
        title = {
            Text(text = title ?: stringResource(id = R.string.app_name)) // TODO: i18n
        },
    )
}

@Preview
@Composable
private fun NavigationDrawerButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = Icons.Outlined.Menu,
    onMenuButtonClick: () -> Unit = {},
) {
    IconButton(onClick = onMenuButtonClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = "", // TODO: desc + i18n
        )
    }
}