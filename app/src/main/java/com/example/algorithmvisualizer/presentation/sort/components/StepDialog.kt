package com.example.algorithmvisualizer.presentation.sort.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue


@Composable
fun StepDialog(
    min: Int,
    maxProvider: () -> Int,
    stepProvider: () -> Int,
    onSetStep: (Int) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    var textState by remember {
        mutableStateOf(
            TextFieldValue(
                text = ""
            )
        )
    }

    var isError: Boolean by remember { mutableStateOf(false) }
    val max = remember { maxProvider() }

    val onConfirmSend: () -> Unit = remember {
        {
            if (!isError) {
                onSetStep(textState.text.toInt())
                onDismissRequest()
            }
        }
    }


    LaunchedEffect(Unit) {
        val text2 = stepProvider().toString()
        textState = textState.copy(text = text2, selection = TextRange(text2.length))
        isError = textState.text.toIntOrNull()?.let { it < min || it > max } ?: true
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Text(text = "OK", modifier = Modifier.clickable {
                onConfirmSend()
            })
        },
        text = {
            TextField(
                value = textState,
                isError = isError,
                onValueChange = { v ->
                    val txtAsDigitsOnly = v.text.filter { c -> c.isDigit() }
                    textState = textState.copy(
                        text = txtAsDigitsOnly,
                        selection = TextRange(txtAsDigitsOnly.length)
                    )

                    val txtAsInt = txtAsDigitsOnly.toIntOrNull()
                    isError = txtAsInt?.let { it < min || txtAsInt > max } ?: true


                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Default
                ),
                keyboardActions = KeyboardActions { onConfirmSend() },
                modifier = modifier.focusRequester(focusRequester)
            )
        })
}
