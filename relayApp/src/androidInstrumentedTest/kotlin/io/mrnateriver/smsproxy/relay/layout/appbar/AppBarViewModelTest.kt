package io.mrnateriver.smsproxy.relay.layout.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppBarViewModelTest {

    @get:Rule
    val rule = createComposeRule()

    private val appBarViewModel = AppBarViewModel()

    @Test
    fun appBarViewModel_shouldSetActions() {
        val actions: @Composable RowScope.() -> Unit = {}

        appBarViewModel.setActions(actions)

        assertTrue(appBarViewModel.actions.value === actions)
    }

    @Test
    fun appBarViewModel_shouldResetActions() {
        val actions: @Composable RowScope.() -> Unit = {}

        appBarViewModel.setActions(actions)
        appBarViewModel.resetActions()

        assertTrue(appBarViewModel.actions.value !== actions)
    }

    @Test
    fun appBarViewModel_overridesActions() {
        val actions1: @Composable RowScope.() -> Unit = {}
        val actions2: @Composable RowScope.() -> Unit = {}

        appBarViewModel.setActions(actions1)
        appBarViewModel.setActions(actions2)

        assertTrue(appBarViewModel.actions.value === actions2)
    }

    @Test
    fun rememberAppBarViewModel_shouldReturnSingletonInstance() {
        var rememberedAppBarViewModel1: AppBarViewModel? = null
        var rememberedAppBarViewModel2: AppBarViewModel? = null
        rule.setContent {
            rememberedAppBarViewModel1 = rememberAppBarViewModel()
            rememberedAppBarViewModel2 = rememberAppBarViewModel()
        }

        assertNotNull(rememberedAppBarViewModel1)
        assertNotNull(rememberedAppBarViewModel2)
        assertTrue(rememberedAppBarViewModel1 === rememberedAppBarViewModel2)
    }
}
