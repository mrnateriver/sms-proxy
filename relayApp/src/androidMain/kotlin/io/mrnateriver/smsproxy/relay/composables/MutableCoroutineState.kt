package io.mrnateriver.smsproxy.relay.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun <T> rememberMutableCoroutineState(
    value: Flow<T>,
    setter: suspend (T) -> Unit,
    defaultValue: T,
): MutableState<T> {
    val scope = rememberCoroutineScope()
    val state = value.collectAsStateWithLifecycle(defaultValue)

    return remember { AsMutableState(state) { scope.launch { setter(it) } } }
}

@Stable
private class AsMutableState<T>(private val state: State<T>, private val setValue: (T) -> Unit) :
    MutableState<T> {
    override var value: T
        get() = state.value
        set(value) {
            setValue(value)
        }

    override fun component1(): T = state.value
    override fun component2(): (T) -> Unit = setValue
}
