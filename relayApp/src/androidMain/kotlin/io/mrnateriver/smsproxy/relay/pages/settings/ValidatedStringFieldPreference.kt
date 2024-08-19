package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.shared.AppPreferencesProvider
import me.zhanghai.compose.preference.TextFieldPreference

@Composable
fun ValidatedStringFieldPreference(
    modifier: Modifier = Modifier,
    validate: (v: String) -> String?,
    state: MutableState<String>,
    title: String,
) {
    val currentValue by state
    var popupValue by remember { mutableStateOf(currentValue) }

    val currentError = validate(popupValue.ifEmpty { currentValue })

    var popupError by remember { mutableStateOf(currentError) }
    var shouldShowPopupError by remember { mutableStateOf(currentValue.isNotEmpty()) }

    TextFieldPreference(
        modifier = modifier.semantics { currentError?.let { error(it) } },
        state = state,
        title = { Text(title) },
        valueToText = { popupValue.ifEmpty { currentValue } },
        textToValue = {
            val popupFieldError = validate(it)

            popupValue = it
            popupError = popupFieldError

            if (popupFieldError.isNullOrBlank()) it else ""
        },
        summary = {
            Text(
                text = (currentError ?: "").ifEmpty { currentValue },
                color = if (currentError.isNullOrEmpty()) Color.Unspecified else MaterialTheme.colorScheme.error,
            )
        },
        textField = { fieldValue, onValueChange, onOk ->
            val popupFieldError = validate(fieldValue.text)
            val isInputInvalid = shouldShowPopupError && !popupFieldError.isNullOrEmpty()

            OutlinedTextField(
                value = fieldValue,
                onValueChange = {
                    shouldShowPopupError = true
                    onValueChange(it)
                },
                modifier = Modifier.fillMaxWidth().semantics { if (isInputInvalid) error(popupFieldError!!) },
                keyboardOptions = KeyboardOptions(autoCorrect = false),
                keyboardActions = KeyboardActions { onOk() },
                singleLine = true,
                isError = isInputInvalid,
                supportingText = {
                    if (isInputInvalid) {
                        Text(popupFieldError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
            )
        }
    )
}

@Preview
@Composable
private fun ValidatedStringFieldPreferencePreview_ValidationError() {
    AppPreferencesProvider {
        ValidatedStringFieldPreference(
            title = "Validated Field",
            state = remember { mutableStateOf("") },
            validate = { "Failed Validation" },
        )
    }
}

@Preview
@Composable
private fun ValidatedStringFieldPreferencePreview_Value() {
    AppPreferencesProvider {
        ValidatedStringFieldPreference(
            title = "Validated Field",
            state = remember { mutableStateOf("Some Value") },
            validate = { null },
        )
    }
}
