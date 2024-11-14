package io.mrnateriver.smsproxy.relay.composables

import androidx.compose.runtime.MutableState
import androidx.compose.ui.test.junit4.createComposeRule
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MutableCoroutineStateTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun rememberMutableCoroutineState_shouldReturnStateWithDefaultValue() {
        var state: MutableState<Int>? = null
        rule.setContent {
            state = rememberMutableCoroutineState(flow { }, {}, 42)
        }

        assertTrue(state?.value == 42)
    }

    @Test
    fun rememberMutableCoroutineState_shouldReturnStateThatIsPopulatedFromFlow() {
        var state: MutableState<Int>? = null
        rule.setContent {
            state = rememberMutableCoroutineState(
                flow {
                    emit(123456)
                },
                {}, 42,
            )
        }

        assertTrue(state?.value == 123456)
    }

    @Test
    fun rememberMutableCoroutineState_stateUpdated_shouldCallProvidedSetter() {
        var calledWith: Int? = null
        var state: MutableState<Int>? = null
        rule.setContent {
            state = rememberMutableCoroutineState(flow { }, { calledWith = it }, 42)
        }

        state?.value = 123456

        assertTrue(calledWith == 123456)
    }
}
