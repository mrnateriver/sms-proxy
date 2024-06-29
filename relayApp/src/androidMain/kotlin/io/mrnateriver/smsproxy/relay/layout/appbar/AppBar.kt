package io.mrnateriver.smsproxy.relay.layout.appbar

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.relay.R

// FIXME: definitely move to shared
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
    navigationIconContentDescription: String? = null,
    navigationIconImageVector: ImageVector = Icons.Outlined.Menu,
    onNavigationButtonClick: () -> Unit = {},
) {
    val contentColor =
        if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary
    val containerColor = rememberRootBackgroundColor()

    val colors = TopAppBarColors(
        containerColor = containerColor,
        scrolledContainerColor = containerColor,
        actionIconContentColor = contentColor,
        titleContentColor = contentColor,
        navigationIconContentColor = contentColor,
    )

    val appBarViewModel = rememberAppBarViewModel()
    val actions by appBarViewModel.actions.collectAsState()

    TopAppBar(
        colors = colors,
        navigationIcon = {
            IconButton(onClick = onNavigationButtonClick) {
                Icon(
                    imageVector = navigationIconImageVector,
                    contentDescription = navigationIconContentDescription,
                )
            }
        },
        title = { Text(text = title ?: stringResource(R.string.app_name)) },
        actions = actions,
    )
}
