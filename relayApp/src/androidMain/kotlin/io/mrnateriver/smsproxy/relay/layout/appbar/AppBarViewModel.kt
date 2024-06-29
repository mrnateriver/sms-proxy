package io.mrnateriver.smsproxy.relay.layout.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val appBarViewModel = AppBarViewModel()

class AppBarViewModel : ViewModel() {
    private val actionsFlow: MutableStateFlow<@Composable RowScope.() -> Unit> =
        MutableStateFlow { }

    val actions: StateFlow<@Composable RowScope.() -> Unit> get() = actionsFlow.asStateFlow()

    fun setActions(actions: @Composable (RowScope.() -> Unit)) {
        actionsFlow.value = actions
    }

    fun resetActions() {
        actionsFlow.value = {}
    }
}

@Composable
fun rememberAppBarViewModel(): AppBarViewModel {
    return appBarViewModel
}

@Composable
fun AppBarActions(
    actions: @Composable RowScope.() -> Unit,
) {
    DisposableEffect(actions) {
        appBarViewModel.setActions(actions)
        onDispose {
            appBarViewModel.resetActions()
        }
    }
}
