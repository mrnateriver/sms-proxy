package io.mrnateriver.smsproxy.relay.settings

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
import me.zhanghai.compose.preference.TextFieldPreference

@Composable
fun ValidatedStringFieldPreference(
    modifier: Modifier = Modifier,
    validate: (v: String) -> String?,
    state: MutableState<String>,
    title: String,
) {
    val currentValue by state
    var currentError by remember { mutableStateOf(validate(currentValue)) }

    var popupValue by remember { mutableStateOf(currentValue) }
    var popupError by remember { mutableStateOf(currentError) }
    var shouldShowPopupError by remember { mutableStateOf(currentValue.isNotEmpty()) }

    TextFieldPreference(
        modifier = modifier,
        state = state,
        title = { Text(title) },
        valueToText = { popupValue.ifEmpty { currentValue } },
        textToValue = {
            currentError = popupError
            if (popupError.isNullOrEmpty()) it else ""
        },
        summary = {
            Text(
                (currentError ?: "").ifEmpty { currentValue },
                color = if (currentError.isNullOrEmpty()) Color.Unspecified else MaterialTheme.colorScheme.error,
            )
        },
        textField = { fieldValue, onValueChange, onOk ->
            OutlinedTextField(
                value = fieldValue,
                onValueChange = {
                    shouldShowPopupError = true
                    popupValue = it.text
                    popupError = validate(it.text)
                    onValueChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(autoCorrect = false),
                keyboardActions = KeyboardActions { onOk() },
                singleLine = true,
                isError = shouldShowPopupError && !popupError.isNullOrEmpty(),
                supportingText = {
                    if (shouldShowPopupError && !popupError.isNullOrEmpty()) {
                        Text(popupError!!, color = MaterialTheme.colorScheme.error)
                    }
                },
            )
        }
    )
}

