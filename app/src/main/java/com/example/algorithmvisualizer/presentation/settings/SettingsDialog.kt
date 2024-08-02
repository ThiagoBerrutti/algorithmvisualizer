//package com.example.algorithmvisualizer.presentation.settings
//
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.widthIn
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.DividerDefaults
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.NonRestartableComposable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//
//@NonRestartableComposable
//@Composable
//fun SettingsDialog(
//    modifier: Modifier = Modifier,
//    onDismiss: () -> Unit,
//    viewModel: SettingsViewModel = hiltViewModel(),
//) {
//    val uiState by viewModel.uiState.collectAsState()
//    val onEvent: (SettingsUiEvent) -> Unit = remember { viewModel::onEvent }
//
//    SettingsDialog(
//        uiState = uiState,
//        onEvent = onEvent,
//        onDismiss = onDismiss,
//        modifier = modifier
//    )
//}
//
//
//@Composable
//fun SettingsDialog(
//    uiState: SettingsUiState,
//    onEvent: (event: SettingsUiEvent) -> Unit,
//    onDismiss: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
////    Column(modifier.padding(horizontal = 20.dp)) {
//    val configuration = LocalConfiguration.current
//
//    AlertDialog(
//        modifier = modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
//        onDismissRequest = onDismiss,
//        confirmButton = {
//            Text(
//                text = "OK",
//                modifier = Modifier
//                    .padding(8.dp)
//                    .clickable {
//                        onEvent(SettingsUiEvent.OnSaveClick)
//                        onDismiss()
//                    }
//            )
//        },
//        text = {
//            HorizontalDivider()
//            Column(
//                Modifier
//                    .padding(horizontal = 20.dp)
//                    .fillMaxSize()
//            ) {
//                when (uiState) {
//                    is SettingsUiState.Success -> {
//                        var opInterval by remember(uiState.delay) { mutableStateOf(uiState.delay) }
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Text(text = "Operation interval (ms)")
//                            TextField(
//                                value = opInterval,
//                                onValueChange = { v -> opInterval = v.filter { c -> c.isDigit() } },
//                                keyboardOptions = KeyboardOptions(
//                                    keyboardType = KeyboardType.Number,
//                                    imeAction = ImeAction.Go
//                                ),
//                                keyboardActions = KeyboardActions(
//                                    onGo = {}
//                                ),
//                                modifier = Modifier.weight(0.5f)
//                            )
//
//
//                        }
//
//                        CheckboxWithLabel(
//                            checked = uiState.isSortInfoVisible,
//                            label = "Show operation info",
//                            onClick = { onEvent(SettingsUiEvent.ShowInfoClick) }
//                        )
//
//                        CheckboxWithLabel(
//                            checked = uiState.showIndices,
//                            label = "Show indices below chart",
//                            onClick = { onEvent(SettingsUiEvent.ShowIndicesClick) }
//                        )
//
//                        CheckboxWithLabel(
//                            checked = uiState.showValues,
//                            label = "Show values",
//                            onClick = { onEvent(SettingsUiEvent.ShowValuesClick) }
//                        )//
//                    }
//
//                    else -> {}
//                }
//            }
//        }
//    )
//}
//
//@Composable
//fun HorizontalDivider(
//    modifier: Modifier = Modifier,
//    thickness: Dp = DividerDefaults.Thickness,
//    color: Color = DividerDefaults.color,
//) = Canvas(modifier.fillMaxWidth().height(thickness)) {
//    drawLine(
//        color = color,
//        strokeWidth = thickness.toPx(),
//        start = Offset(0f, thickness.toPx() / 2),
//        end = Offset(size.width, thickness.toPx() / 2),
//    )
//}
//
//
//
