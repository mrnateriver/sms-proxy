package io.mrnateriver.smsproxy.relay.pages.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import io.mrnateriver.smsproxy.shared.composables.AppPreferencesProvider
import me.zhanghai.compose.preference.TextFieldPreference

@Composable
fun ValidatedStringFieldPreference(
    value: String,
    title: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    validate: (v: String) -> String?,
) {
    var popupValue by remember { mutableStateOf(value) }

    val currentError = validate(popupValue.ifEmpty { value })

    var popupError by remember { mutableStateOf(currentError) }
    var shouldShowPopupError by remember { mutableStateOf(value.isNotEmpty()) }

    TextFieldPreference(
        modifier = modifier.semantics { currentError?.let { error(it) } },
        value = value,
        onValueChange = onValueChange,
        title = { Text(title) },
        valueToText = { popupValue.ifEmpty { value } },
        textToValue = {
            val popupFieldError = validate(it)

            popupValue = it
            popupError = popupFieldError

            if (popupFieldError.isNullOrBlank()) it else ""
        },
        summary = {
            Text(
                text = currentError.orEmpty().ifEmpty { value },
                color = if (currentError.isNullOrEmpty()) Color.Unspecified else MaterialTheme.colorScheme.error,
            )
        },
        textField = { fieldValue, onFieldValueChange, onOk ->
            val popupFieldError = validate(fieldValue.text)
            val isInputInvalid = shouldShowPopupError && !popupFieldError.isNullOrEmpty()

            OutlinedTextField(
                value = fieldValue,
                onValueChange = {
                    shouldShowPopupError = true
                    onFieldValueChange(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { if (isInputInvalid && popupFieldError != null) error(popupFieldError) },
                keyboardOptions = KeyboardOptions(autoCorrectEnabled = false),
                keyboardActions = KeyboardActions { onOk() },
                singleLine = true,
                isError = isInputInvalid,
                supportingText = {
                    if (isInputInvalid && popupFieldError != null) {
                        Text(popupFieldError, color = MaterialTheme.colorScheme.error)
                    }
                },
            )
        },
    )
}

@Preview
@Composable
private fun ValidatedStringFieldPreferencePreview_ValidationError() {
    AppPreferencesProvider {
        ValidatedStringFieldPreference(
            title = "Validated Field",
            value = "",
            onValueChange = {},
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
            value = "Some Value",
            onValueChange = {},
            validate = { null },
        )
    }
}
