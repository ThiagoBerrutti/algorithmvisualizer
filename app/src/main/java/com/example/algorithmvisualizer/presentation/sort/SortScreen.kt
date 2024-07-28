package com.example.algorithmvisualizer.presentation.sort

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.algorithmvisualizer.R
import com.example.algorithmvisualizer.domain.model.SortAlgorithmName
import com.example.algorithmvisualizer.domain.model.name

@NonRestartableComposable
@Composable
fun SortRoute(
    viewModel: SortViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()


//    val currentStep by viewModel.currentStep.collectAsState()
//    val currentOperation by viewModel.currentOperation.collectAsState()
//    val currentState by viewModel.currentState.collectAsState()

//    val getOperationSize: () -> Int =  viewModel::getOperationSize


    val onSlideChange: (Float) -> Unit = remember(viewModel) { viewModel::onSlideChange }
    val onPrevStep: () -> Unit = remember(viewModel) { viewModel::onClickPrevStep }
    val onPlay: () -> Unit = remember(viewModel) { viewModel::onPlay }
    val onNextStep: () -> Unit = remember(viewModel) { viewModel::onClickNextStep }
    val onResetClick: () -> Unit = remember(viewModel) { viewModel::onResetClick }
    val isPlaying by viewModel.isPlaying.collectAsState()
//    val isSortInfoVisible by viewModel.isSortInfoVisible.collectAsState()


    val algorithm by viewModel.algorithmName.collectAsState()
    val onAlgorithmChange: (SortAlgorithmName) -> Unit =
        viewModel::onAlgorithmChanged

//    LaunchedEffect(System.currentTimeMillis()) {
//        Log.d("SORTROUTE_COMP_TEST", "------")
//    }


//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
//    val openDrawer: () -> Unit = remember {
//        {
//            scope.launch {
//                drawerState.open()
//            }
//        }
//    }
//
//    val closeDrawer: () -> Unit = remember {
//        {
//            scope.launch {
//                drawerState.close()
//            }
//        }
//    }

//    LaunchedEffect(uiState) {
////        Log.d("")
//        Log.d("SORTROUTE_COMP_TEST", "---")
//        Log.d("SORTROUTE_COMP_TEST", "uiState: $uiState")
//    }

//    LaunchedEffect(currentStep) {
//        Log.d("SORTROUTE_COMP_TEST","currentStep:$currentStep")
//    }

//    LaunchedEffect(viewModel) {
//        Log.d("SORTROUTE_COMP_TEST", "viewModel:$viewModel")
//    }

//    LaunchedEffect(currentOperation) {
//        Log.d("SORTROUTE_COMP_TEST", "currentOperation:$currentOperation")
//    }
//    LaunchedEffect(currentState) {
//        Log.d("SORTROUTE_COMP_TEST","currentState: $currentState")
//    }

//    LaunchedEffect(isPlaying) {
//        Log.d("SORTROUTE_COMP_TEST", "isPlaying: $isPlaying")
//    }


//    LaunchedEffect(isSortInfoVisible) {
//        Log.d("SORTROUTE_COMP_TEST","isSortInfoVisible: $isSortInfoVisible")
//    }

//    LaunchedEffect(getOperation) {
//        Log.d("SORTSCREEN_COMP_TEST","getOperation")
//    }

//    LaunchedEffect(getOperationSize) {
//        Log.d("SORTROUTE_COMP_TEST","getOperationSize: $getOperationSize")
//    }

//    LaunchedEffect(uiState) {
//        Log.d("SORTSCREEN_COMP_TEST","itemCount")
//    }
//
//    LaunchedEffect(uiState) {
//        Log.d("SORTSCREEN_COMP_TEST","itemCount")
//    }
//
//    LaunchedEffect(uiState) {
//        Log.d("SORTSCREEN_COMP_TEST","itemCount")
//    }


//    AlgorithmNameDrawer(
//        state = drawerState,
//        onAlgorithmChange = onAlgorithmChange,
//        closeDrawer = closeDrawer
//    ) {

//    val duration = measureTimeMillis {

    SortScreen(
        onNextStep = onNextStep,
        onPlay = onPlay,
        onPrevStep = onPrevStep,
        getOperationSize = viewModel::getOperationSize,
        isPlaying = isPlaying,
        onSlideChange = onSlideChange,
        algorithm = algorithm,
        onAlgorithmChange = onAlgorithmChange,
        onResetClick = onResetClick,
        uiState = uiState,
        modifier = Modifier
    )
//    }
}


@Composable
fun SortScreen(
    uiState: SortScreenUiState,
    getOperationSize: () -> Int,
    onSlideChange: (Float) -> Unit,
    onPrevStep: () -> Unit,
    onPlay: () -> Unit,
    onNextStep: () -> Unit,
    onResetClick: () -> Unit,
    isPlaying: Boolean,

    algorithm: SortAlgorithmName,
    onAlgorithmChange: (SortAlgorithmName) -> Unit,

//    onDrawerOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
//    val rUiState = remember { uiState }
//    LaunchedEffect(uiState) {
//        Log.d("SORTSCREEN_COMP_TEST", "rUiState: ${uiState}")
//    }
//
//        LaunchedEffect(currentStep) {
//        Log.d("SORTSCREEN_COMP_TEST","currentStep: $currentStep")
//    }
//    LaunchedEffect(currentOperation) {
//        Log.d("SORTSCREEN_COMP_TEST", "currentOperation: $currentOperation")
//    }
//    LaunchedEffect(currentState) {
//        Log.d("SORTSCREEN_COMP_TEST","currentState: $currentState")
//    }
    LaunchedEffect(uiState) {
        if (uiState is SortScreenUiState.Completed) {
            Log.d(
                "SORTSCREEN_COMP_TEST",
                "uiState: Completed; " +
                        "currentStep: ${uiState.currentStep}; " +
                        "isSortInfoVisible: ${uiState.isSortInfoVisible}; " +
                        "showValues: ${uiState.showValues}; " +
                        "showIndices: ${uiState.showIndices}"
            )
        } else {
            Log.d("SORTSCREEN_COMP_TEST", "uiState: $uiState")
        }
    }
//    LaunchedEffect(getOperationSize) {
//        Log.d("SORTSCREEN_COMP_TEST", "getOperationSize: $getOperationSize")
//    }
//    LaunchedEffect(isPlaying) {
//        Log.d("SORTSCREEN_COMP_TEST", "isPlaying: $isPlaying")
//    }
//    LaunchedEffect(isSortInfoVisible) {
//        Log.d("SORTSCREEN_COMP_TEST","isSortInfoVisible: $isSortInfoVisible")
//    }
//    LaunchedEffect(onSlideChange) {
//        Log.d("SORTSCREEN_COMP_TEST", "onSlideChange: $onSlideChange")
//    }


//    LaunchedEffect(onResetClick) {
//        Log.d("SORTSCREEN_COMP_TEST", "onResetClick: $onResetClick")
//    }
//    LaunchedEffect(onNextStep) {
//        Log.d("SORTSCREEN_COMP_TEST", "onNextStep: $onNextStep")
//    }
//    LaunchedEffect(onPrevStep) {
//        Log.d("SORTSCREEN_COMP_TEST", "onPrevStep: $onPrevStep")
//    }
//    LaunchedEffect(algorithm) {
//        Log.d("SORTSCREEN_COMP_TEST", "algorithm: $algorithm")
//    }
//    LaunchedEffect(onAlgorithmChange) {
//        Log.d("SORTSCREEN_COMP_TEST", "onAlgorithmChange: $onAlgorithmChange")
//    }

//    val rCurrentStep = remember(currentStep) { currentStep }
    var expanded by remember { mutableStateOf(false) }
    val onDismissRequest = remember { { expanded = false } }

//    val uiIsLoading = uiState is SortScreenlUiState.Completed

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .border(1.dp, Color.Magenta),
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
//                        onDrawerOpen

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


//            Row {
//                Text(
//                    text = algorithm.name ?: "",
//                    fontSize = 40.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.clickable {
//                        onDrawerOpen()
////                    expanded = true
//                    }
//                )
//            }
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = { expanded = false }
//            ) {
//                SortAlgorithmName.entries.forEach { item ->
//                    Log.d("ALGO_NAMME_TEST", item.name)
//                    DropdownMenuItem(
//                        text = { Text(item.name, fontSize = 32.sp) },
//                        onClick = {
//                            onAlgorithmChange(item);
//                            expanded = false
//                        })
//                }
//            }
//        }
//        Column(        modifier = modifier
//            .fillMaxSize()            ,
//            verticalArrangement = Arrangement.SpaceBetween,
//            horizontalAlignment = Alignment.CenterHorizontally) {


        if (expanded) {
            AlgorithmNameDialog(
                onAlgorithmChange,
                onDismissRequest,
                onCompletion = onDismissRequest
            )
        }
        Column(Modifier.weight(1f)) {
            if (uiState is SortScreenUiState.Completed) {
                if (uiState.isSortInfoVisible) {
                    Row {
                        Text(
                            text = uiState.currentOperation?.action?.name ?: "",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Row {
                        Text(
                            text = "(${uiState.currentOperation?.indices?.joinToString(separator = ", ")})",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${
                                uiState.currentOperation?.indices?.map { i ->
                                    uiState.items.elementAtOrNull(i)?.copy()?.value ?: ""
                                }
                                    ?.joinToString(separator = ", ")
                            }", fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(Modifier.weight(1f)) {
                    BarChart(
                        items = uiState.items,
                        showIndices = uiState.showIndices,
                        showValues = uiState.showValues
                    )
                }

                SortSteps(
                    uiState.currentStep,
                    onSlideChange,
                    getOperationSize,
                    Modifier.padding(horizontal = 20.dp)
                )
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

    val iconButtonColors = IconButtonDefaults.filledIconButtonColors(

    )
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
fun SortSteps(
    step: Int,
    onSliderValueChange: (Float) -> Unit,
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Step: ", fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Text(text = stepText, fontWeight = FontWeight.Medium, fontSize = 24.sp)
        }
    }

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

@Composable
fun AlgorithmNameDrawer(
    state: DrawerState, modifier: Modifier = Modifier,
    onAlgorithmChange: (SortAlgorithmName) -> Unit,
    closeDrawer: () -> Unit,
    content: @Composable () -> Unit,
) {

    ModalNavigationDrawer(
        drawerState = state,
        drawerContent = { DrawerContent(onAlgorithmChange, closeDrawer) },
        content = content,
        modifier = modifier
    )
}

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



