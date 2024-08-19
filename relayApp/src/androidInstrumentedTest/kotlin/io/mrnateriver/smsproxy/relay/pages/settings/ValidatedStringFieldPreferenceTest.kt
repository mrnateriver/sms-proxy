package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isEditable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.requestFocus
import io.mrnateriver.smsproxy.shared.AppPreferencesProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ValidatedStringFieldPreferenceTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun validatedStringFieldPreference_shouldShowProvidedTitle() {
        rule.setContent {
            AppPreferencesProvider {
                ValidatedStringFieldPreference(
                    title = "Test Title",
                    state = remember { mutableStateOf("") },
                    validate = { null }
                )
            }
        }

        rule.onNodeWithText("Test Title").assertExists()
    }

    @Test
    fun validatedStringFieldPreference_shouldShowProvidedValue() {
        rule.setContent {
            AppPreferencesProvider {
                ValidatedStringFieldPreference(
                    title = "Test Title",
                    state = remember { mutableStateOf("Test Value 42") },
                    validate = { null }
                )
            }
        }

        rule.onNodeWithText("Test Value 42").assertExists()
    }

    @Test
    fun validatedStringFieldPreference_shouldShowErrorWhenInputIsInvalid() {
        rule.setContent {
            AppPreferencesProvider {
                ValidatedStringFieldPreference(
                    title = "Test Title",
                    state = remember { mutableStateOf("Test Value 42") },
                    validate = { "Test Invalid Input" }
                )
            }
        }

        rule.onNodeWithText("Test Invalid Input").assertExists()
        rule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Test Invalid Input"))
            .assertExists()
    }

    @Test
    fun validatedStringFieldPreference_shouldRevalidateInputIfChangedFromUpstream() {
        val state = mutableStateOf("")
        rule.setContent {
            AppPreferencesProvider {
                ValidatedStringFieldPreference(
                    title = "Test Title",
                    state = state,
                    validate = { if (it.isEmpty()) null else "Test Invalid Input" }
                )
            }
        }

        rule.onNodeWithText("Test Invalid Input").assertDoesNotExist()

        state.value = "Test Value 42"

        rule.onNodeWithText("Test Invalid Input").assertExists()
        rule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Test Invalid Input"))
            .assertExists()
    }

    @Test
    fun validatedStringFieldPreference_shouldOpenAPopupOnClick() {
        rule.setContent {
            AppPreferencesProvider {
                ValidatedStringFieldPreference(
                    title = "Test Title",
                    state = remember { mutableStateOf("Test Value 42") },
                    validate = { null }
                )
            }
        }

        rule.onNodeWithText("Test Value 42").performClick()

        rule.onNode(isDialog()).assertExists()
    }

    @Test
    fun validatedStringFieldPreference_popup_shouldShowValue() {
        rule.setContent {
            AppPreferencesProvider {
                ValidatedStringFieldPreference(
                    title = "Test Title",
                    state = remember { mutableStateOf("Test Value 42") },
                    validate = { null }
                )
            }
        }

        rule.onNodeWithText("Test Value 42").performClick()

        rule.onAllNodesWithText("Test Value 42").assertCountEquals(2)
    }

    @Test
    fun validatedStringFieldPreference_popup_shouldShowValidationErrorBeforeInput() {
        rule.setContent {
            AppPreferencesProvider {
                ValidatedStringFieldPreference(
                    title = "Test Title",
                    state = remember { mutableStateOf("Test Value 42") },
                    validate = { "Test Invalid Input" }
                )
            }
        }

        rule.onNodeWithText("Test Invalid Input").performClick()

        rule.onAllNodesWithText("Test Invalid Input").assertCountEquals(2)
    }

    @Test
    fun validatedStringFieldPreference_popup_shouldShowValidationErrorAfterInput() {
        rule.setContent {
            AppPreferencesProvider {
                ValidatedStringFieldPreference(
                    title = "Test Title",
                    state = remember { mutableStateOf("Test Value 42") },
                    validate = { if (it == "Test Value 42") null else "Test Invalid Input" }
                )
            }
        }

        rule.onNodeWithText("Test Value 42").performClick()

        rule.onAllNodesWithText("Test Invalid Input").assertCountEquals(0)

        rule.onAllNodesWithText("Test Value 42")
            .filterToOne(isEditable())
            .requestFocus()
            .performTextInput("3")

        rule.onAllNodesWithText("Test Invalid Input").assertCountEquals(1)
    }

    @Test
    fun validatedStringFieldPreference_popup_shouldSetProvidedStateValueOnPopupSubmit() {
        val state = mutableStateOf("")
        rule.setContent {
            AppPreferencesProvider {
                ValidatedStringFieldPreference(
                    title = "Test Title",
                    state = state,
                    validate = { null }
                )
            }
        }

        rule.onNodeWithText("Test Title").performClick() // Clicking on title also works

        rule.onNode(isEditable())
            .requestFocus()
            .performTextInput("hello")

        rule.onNodeWithText(rule.activity.getString(android.R.string.ok)).performClick()

        assertEquals("hello", state.value)
    }

    @Test
    fun validatedStringFieldPreference_popup_shouldNotSetProvidedStateValueOnInvalidPopupSubmit() {
        val state = mutableStateOf("hello")
        rule.setContent {
            AppPreferencesProvider {
                ValidatedStringFieldPreference(
                    title = "Test Title",
                    state = state,
                    validate = { if (it == "hello") null else "Test Invalid Input" }
                )
            }
        }

        rule.onNodeWithText("hello").performClick()

        rule.onNode(isEditable())
            .requestFocus()
            .performTextInput("hey")

        rule.onNodeWithText(rule.activity.getString(android.R.string.ok)).performClick()

        assertEquals("", state.value)
    }
}