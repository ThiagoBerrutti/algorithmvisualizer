package com.example.algorithmvisualizer.presentation.sort

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.algorithmvisualizer.R
import com.example.algorithmvisualizer.data.util.ISortOperation
import com.example.algorithmvisualizer.data.util.QuickSortOperation
import com.example.algorithmvisualizer.data.util.SortOperationHelper
import com.example.algorithmvisualizer.domain.model.QuickSortAction
import com.example.algorithmvisualizer.domain.model.SortAlgorithmName
import com.example.algorithmvisualizer.presentation.ui.AlgoVisAppState
import com.example.algorithmvisualizer.presentation.ui.AppBottomNavigationBar
import com.example.algorithmvisualizer.presentation.ui.theme.AlgorithmVisualizerTheme
import com.example.algorithmvisualizer.presentation.utils.generateStaticItems

@NonRestartableComposable
@Composable
fun SortRoute(
    viewModel: SortViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val onSlideChange: (Float) -> Unit = remember(viewModel) { viewModel::onSlideChange }
    val onPrevStep: () -> Unit = remember(viewModel) { viewModel::onClickPrevStep }
    val onPlay: () -> Unit = remember(viewModel) { viewModel::onPlay }
    val onNextStep: () -> Unit = remember(viewModel) { viewModel::onClickNextStep }
    val onResetClick: () -> Unit = remember(viewModel) { viewModel::onResetClick }
    val isPlaying by viewModel.isPlaying.collectAsState()
    val onSetStep: (Int) -> Unit = remember(viewModel) { viewModel::onStepValueChange }


    val algorithm by viewModel.algorithmName.collectAsState()
    val onAlgorithmChange: (SortAlgorithmName) -> Unit =
        viewModel::onAlgorithmChanged


    SortScreen(
        onNextStep = onNextStep,
        onPlay = onPlay,
        onPrevStep = onPrevStep,
        onSetStep = onSetStep,
        getOperationSize = viewModel::getOperationSize,
        isPlaying = isPlaying,
        onSlideChange = onSlideChange,
        algorithm = algorithm,
        onAlgorithmChange = onAlgorithmChange,
        onResetClick = onResetClick,
        uiState = uiState,
        modifier = Modifier
    )
}


@Composable
fun SortScreen(
    uiState: SortScreenUiState,
    getOperationSize: () -> Int,
    onSlideChange: (Float) -> Unit,
    onSetStep: (Int) -> Unit,
    onPrevStep: () -> Unit,
    onPlay: () -> Unit,
    onNextStep: () -> Unit,
    onResetClick: () -> Unit,
    isPlaying: Boolean,

    algorithm: SortAlgorithmName,
    onAlgorithmChange: (SortAlgorithmName) -> Unit,
    modifier: Modifier = Modifier,
) {

    var expanded by remember { mutableStateOf(false) }
    val onDismissRequest = remember { { expanded = false } }

    var showStepDialog by remember { mutableStateOf(false) }
    val onStepDialogDismiss = remember { { showStepDialog = false } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp)
            ) {
                Text(
                    text = algorithm.name,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                IconButton(
                    onClick = { expanded = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        tint = Color.White,
                        contentDescription = "menu",
                        modifier = Modifier.size(40.dp, 40.dp)
                    )
                }
            }
        }

        if (expanded) {
            AlgorithmNameDialog(
                onAlgorithmChange,
                onDismissRequest,
                onCompletion = onDismissRequest
            )
        }
        Spacer(Modifier.height(20.dp))

        Column(Modifier.weight(1f)) {
            if (uiState is SortScreenUiState.Completed) {
                if (uiState.isSortInfoVisible) {
                    SortOperationInfo(
                        operation = uiState.currentOperation,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.05f))
//                            .padding(vertical = 12.dp, horizontal = 24.dp)
//                            .height(60.dp)
                            .fillMaxWidth()
                    )
                }

                Row(Modifier.weight(1f)) {
                    BarChart(
                        itemList = uiState.items,
                        showIndices = uiState.showIndices,
                        showValues = uiState.showValues,
                    )
                }

                SortSteps(
                    step = uiState.currentStep,
                    onSliderValueChange = onSlideChange,
                    onStepClick = { showStepDialog = true },
                    getOperationSize = getOperationSize,
                    modifier = Modifier.padding(horizontal = 20.dp)

                )

                if (showStepDialog) {
                    StepDialog(
                        min = 0,
                        maxProvider = getOperationSize,
                        stepProvider = { uiState.currentStep },
                        onSetStep = onSetStep,
                        onDismissRequest = onStepDialogDismiss)
                }
            }
        }

        SortControls(
            onPlay = onPlay,
            onPrevStep = onPrevStep,
            onNextStep = onNextStep,
            isPlaying = isPlaying,
            onResetClick = onResetClick
        )
    }

}


@Composable
fun SortControls(
    onPrevStep: () -> Unit,
    onPlay: () -> Unit,
    onNextStep: () -> Unit,
    onResetClick: () -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val rOnPlay = remember { { onPlay() } }

    val iconButtonColors = IconButtonDefaults.filledIconButtonColors()
    val pauseColors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    )

    Column(
        modifier = modifier.padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.wrapContentHeight()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Reset")
                Spacer(Modifier.height(4.dp))
                IconButton(
                    onClick = onResetClick,
                    colors = iconButtonColors,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(painterResource(id = R.drawable.ic_rotate_cw), "prev")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Prev")
                Spacer(Modifier.height(4.dp))
                IconButton(
                    onClick = onPrevStep,
                    colors = iconButtonColors,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(painterResource(id = R.drawable.ic_chevron_left), "prev")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val text: String
                val colors = if (isPlaying) pauseColors else iconButtonColors

                val icon = if (isPlaying) {
                    text = "Pause"
                    R.drawable.ic_pause
                } else {
                    text = "Play"
                    R.drawable.ic_play
                }

                Text(text)
                Spacer(Modifier.height(4.dp))
                IconButton(
                    onClick = rOnPlay,
                    colors = colors,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(painterResource(id = icon), "play")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Next")
                Spacer(Modifier.height(4.dp))
                IconButton(
                    onClick = onNextStep,
                    colors = iconButtonColors,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(painterResource(id = R.drawable.ic_chevron_right), "next")
                }
            }
        }
    }
}

@Composable
fun SortOperationInfo(
    operation: ISortOperation?,
    modifier: Modifier = Modifier,
) {
    val opHelper = remember { SortOperationHelper() }
    val text = remember(operation) { operation?.let { opHelper.createMessage(operation) } ?: "" }

    var expanded by remember { mutableStateOf(true) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .animateContentSize(
                animationSpec = tween(300)
            )
            .clickable { expanded = !expanded }
            .padding(vertical = 12.dp, horizontal = 24.dp),
    ) {
        if (expanded) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.height(60.dp)
            )
        }
    }
}

@Composable
fun SortSteps(
    step: Int,
    onSliderValueChange: (Float) -> Unit,
    onStepClick: () -> Unit,
    getOperationSize: () -> Int,
    modifier: Modifier = Modifier,
) {
    val opSize = getOperationSize()
    val stepText = remember(step) { "$step" }
    val sliderPosition by remember(opSize, step) {
        mutableFloatStateOf(
            if (opSize == 0) {
                0f
            } else {
                (step.toFloat() / opSize)
            }
        )
    }

    Column(modifier) {
        Slider(
            value = sliderPosition,
            onValueChange = onSliderValueChange,
            steps = opSize,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onStepClick() }
        ) {
            Text("Step: ", fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Text(text = stepText, fontWeight = FontWeight.Medium, fontSize = 24.sp)
        }
    }

}

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
//    var text by remember { mutableStateOf("")}
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
        val text2 = stepProvider().toString() ?: "999999"
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


@Composable
fun AlgorithmNameDialog(
    onAlgorithmChange: (SortAlgorithmName) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    onCompletion: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SortAlgorithmName.entries.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onAlgorithmChange(it)
                            onCompletion()
                        }
                ) {
                    Text(
                        text = it.name,
                        fontSize = 32.sp,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }

            }
        }
    }
}

//@Composable
//fun AlgorithmNameDrawer(
//    state: DrawerState, modifier: Modifier = Modifier,
//    onAlgorithmChange: (SortAlgorithmName) -> Unit,
//    closeDrawer: () -> Unit,
//    content: @Composable () -> Unit,
//) {
//
//    ModalNavigationDrawer(
//        drawerState = state,
//        drawerContent = { DrawerContent(onAlgorithmChange, closeDrawer) },
//        content = content,
//        modifier = modifier
//    )
//}

@Composable
fun DrawerContent(
    onAlgorithmChange: (SortAlgorithmName) -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(modifier = modifier) {
        SortAlgorithmName.entries.map {
            NavigationDrawerItem(label = { Text(it.name) }, selected = false,
                onClick = {
                    onAlgorithmChange(it)
                    closeDrawer()
                })

        }
    }
}

//@Composable
//fun SettingsDialog(
//    onDismissRequest: () -> Unit,
//    modifier: Modifier = Modifier) {
//    Dialog(onDismissRequest = onDismissRequest) {
//        SettingsRoute(
//            onBackClick = {},
//            modifier = modifier
//                .background(MaterialTheme.colorScheme.surfaceVariant)
////                .fillMaxSize(0.8f)
//                    )
//
//    }
//}

@Preview(
    apiLevel = 34,
    device = Devices.PIXEL_7,
//    heightDp = 840,
//    widthDp = 411,
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun SortScreenPreview() {
    AlgorithmVisualizerTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { AppBottomNavigationBar(appState = AlgoVisAppState(rememberNavController())) }
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(it)
                    .fillMaxSize()

            ) {
                val itemList = ItemList(generateStaticItems(15), "")
                val uiState = SortScreenUiState.Completed(
                    items = itemList,
                    isSortInfoVisible = true,
                    showIndices = true,
                    currentStep = 3,
                    currentOperation = QuickSortOperation(
                        QuickSortAction.Swapping,
                        listOf(1, 2),
                        listOf(itemList.items[1], itemList.items[2])
                    ),
                    showValues = true
                )

                SortScreen(
                    uiState = uiState,
                    getOperationSize = { 1 },
                    onSlideChange = {},
                    onSetStep = {},
                    onPrevStep = { },
                    onPlay = { },
                    onNextStep = { },
                    onResetClick = { },
                    isPlaying = true,
                    algorithm = SortAlgorithmName.QuickSort,
                    onAlgorithmChange = {}
                )
            }
        }
    }
}





