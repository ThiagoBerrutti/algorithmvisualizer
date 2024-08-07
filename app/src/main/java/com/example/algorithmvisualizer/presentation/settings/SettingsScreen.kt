package com.example.algorithmvisualizer.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.algorithmvisualizer.presentation.settings.SettingsUiEvent.SaveDelayClick
import com.example.algorithmvisualizer.presentation.settings.SettingsUiEvent.ShowIndicesClick
import com.example.algorithmvisualizer.presentation.settings.SettingsUiEvent.ShowInfoClick
import com.example.algorithmvisualizer.presentation.settings.SettingsUiEvent.ShowValuesClick


@NonRestartableComposable
@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    isDialog: Boolean = false,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val onEvent: (SettingsUiEvent) -> Unit = remember { viewModel::onEvent }

    SettingsScreen(
        uiState = uiState,
        onEvent = onEvent,
        onBackClick = onBackClick,
        isDialog = isDialog,
        modifier = modifier
    )
}


@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onEvent: (event: SettingsUiEvent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDialog: Boolean = false,
) {
//    Column(modifier.padding(horizontal = 20.dp)) {


    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (!isDialog) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.TwoTone.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxSize()
        ) {
            when (uiState) {
                is SettingsUiState.Success -> {
                    var opInterval by remember(uiState.delay) { mutableStateOf(uiState.delay) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Operation interval (ms)")
                        TextField(
                            value = opInterval,
                            onValueChange = { v -> opInterval = v.filter { c -> c.isDigit() } },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Go
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = { }
                            ),
                            modifier = Modifier.weight(0.5f)
                        )

                        if (!isDialog) {
                            Button(onClick = { onEvent(SaveDelayClick(opInterval)) }) {
                                Text("Save")
                            }
                        }
                    }

                    CheckboxWithLabel(
                        checked = uiState.isSortInfoVisible,
                        label = "Show operation info",
                        onClick = { onEvent(ShowInfoClick) }
                    )

                    CheckboxWithLabel(
                        checked = uiState.showIndices,
                        label = "Show indices below chart",
                        onClick = { onEvent(ShowIndicesClick) }
                    )

                    CheckboxWithLabel(
                        checked = uiState.showValues,
                        label = "Show values",
                        onClick = { onEvent(ShowValuesClick) }
                    )
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .height(48.dp)
//                            .clickable { onEvent(ShowInfoClick) }
//                    ) {
//                        Checkbox(
//                            checked = uiState.isSortInfoVisible,
//                            onCheckedChange = null,
//                        )
//                        Text(text = "Show operation info")
//                    }
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .height(48.dp)
//                            .clickable { onEvent(ShowIndicesClick) }
//                    ) {
//                        Checkbox(
////                            checked = showIndices,
//                            checked = uiState.showIndices,
//                            onCheckedChange = null
//                        )
//                        Text(text = "Show indices below chart")
//                    }
//
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(12.dp),
//                        modifier = Modifier
//                            .height(48.dp)
//                            .clickable { onEvent(ShowValuesClick) }
//                    ){
//                        Checkbox(
//                            checked = uiState.showValues,
//                            onCheckedChange = null
//                        )
//                        Text(text = "Show values", maxLines = 1, overflow = TextOverflow.Ellipsis)
//                    }
                }

                else -> {}
            }
        }


    }
}

@NonRestartableComposable
@Composable
fun CheckboxWithLabel(
    checked: Boolean,
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )
        Text(text = label, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}