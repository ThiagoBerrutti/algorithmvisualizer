package com.example.algorithmvisualizer.presentation.sort

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
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
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.algorithmvisualizer.R
import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.SortAction
import com.example.algorithmvisualizer.domain.model.SortAlgorithmName
import com.example.algorithmvisualizer.domain.model.SortOperation
import com.example.algorithmvisualizer.domain.model.name
import com.example.algorithmvisualizer.presentation.components.BarItem
import kotlinx.coroutines.launch


@Composable
fun SortRoute(
    modifier: Modifier = Modifier,
    viewModel: SortViewModel = hiltViewModel(),
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val currentOperation by viewModel.currentOperation.collectAsState()
    val currentState by viewModel.currentState.collectAsState()

    val getOperation = remember { { viewModel.currentOperation.value } }
    val getOperationSize = viewModel.getOperationSize


    val onSlideChange: (Float) -> Unit = remember { viewModel::onSlideChange }
    val onPrevStep: () -> Unit = remember { viewModel::onClickPrevStep }
    val onPlay: () -> Unit = remember { viewModel::onPlay }
    val onNextStep: () -> Unit = remember { viewModel::onClickNextStep }
    val onResetClick: () -> Unit = remember { viewModel::onResetClick }
    val isPlaying by viewModel.isPlaying.collectAsState()

    val delay by viewModel.delay.collectAsState(0)
    val setDelay: (Long) -> Unit = remember { viewModel::setDelay }

    val onRandom: () -> Unit = remember { viewModel::onRandomClick }


    val algorithm by viewModel.algorithmName.collectAsState()
    val onAlgorithmChange: (SortAlgorithmName) -> Unit =
        remember { viewModel::onAlgorithmChanged }

    LaunchedEffect(currentState) {
        Log.d("QUICK_TEST_items", currentState.joinToString { it.value.toString() })
    }


    LaunchedEffect(currentOperation) {
        Log.d("QUICK_TEST_operation", "${currentOperation?.action}")
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val openDrawer: () -> Unit = remember {
        {
            scope.launch {
                drawerState.open()
            }
        }
    }

    val closeDrawer: () -> Unit = remember {
        {
            scope.launch {
                drawerState.close()
            }
        }
    }

    AlgorithmNameDrawer(
        state = drawerState,
        onAlgorithmChange = onAlgorithmChange,
        closeDrawer = closeDrawer
    ) {

        SortScreen(
            onNextStep = onNextStep,
            setDelay = setDelay,
            onPlay = onPlay,
            onPrevStep = onPrevStep,
            currentOperation = currentOperation,
            getOperation = getOperation,
            getOperationSize = getOperationSize,
            delay = delay,
            isPlaying = isPlaying,
            currentState = currentState,
            onSlideChange = onSlideChange,
            currentStep = currentStep,
            onRandom = onRandom,
            algorithm = algorithm,
            onAlgorithmChange = onAlgorithmChange,
            onDrawerOpen = openDrawer,
            onResetClick = onResetClick
        )
    }
}

@Composable
internal fun SortScreen(
    currentStep: Int,
    currentOperation: SortOperation<SortAction>?,
    currentState: List<Item>,

    getOperation: () -> SortOperation<SortAction>?,
    getOperationSize: () -> Int,

    onSlideChange: (Float) -> Unit,
    onPrevStep: () -> Unit,
    onPlay: () -> Unit,
    onNextStep: () -> Unit,
    onRandom: () -> Unit,
    onResetClick: () -> Unit,
    isPlaying: Boolean,
    delay: Long,
    setDelay: (Long) -> Unit,

    algorithm: SortAlgorithmName,
    onAlgorithmChange: (SortAlgorithmName) -> Unit,

    onDrawerOpen: () -> Unit,
) {

    val rCurrentStep = remember(currentStep) { currentStep.toFloat() }
    var expanded by remember { mutableStateOf(false) }
    val onDismissRequest = remember { { expanded = false } }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
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
                    fontSize = 24.sp
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                IconButton(
                    onClick =
                    onDrawerOpen

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




        Row {
            Text(
                text = algorithm.name ?: "",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onDrawerOpen()
//                    expanded = true
                }
            )
        }
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

        if (expanded) {
            AlgorithmNameDialog(
                onAlgorithmChange,
                onDismissRequest,
                onCompletion = onDismissRequest
            )
        }

        Row {
            Text(
                text = currentOperation?.action?.name ?: "",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row {
            Text(
                text = "(${currentOperation?.indices?.joinToString(separator = ", ")})",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${
                    currentOperation?.indices?.map { i -> currentState.elementAtOrNull(i)?.value ?: "" }
                        ?.joinToString(separator = ", ")
                }", fontSize = 24.sp, fontWeight = FontWeight.SemiBold
            )
        }
        SortInfoView(currentState)

        Button(
            onClick = onRandom,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta)
        ) {
            Text(text = "RANDOM")
        }

        SortControls(
            onSliderValueChange = onSlideChange,
            onPlay = onPlay,
            onPrevStep = onPrevStep,
            onNextStep = onNextStep,
            step = rCurrentStep,
            getOperationSize = getOperationSize,
            isPlaying = isPlaying,
            setDelay = setDelay,
            delay = delay,
            onResetClick = onResetClick
        )
    }
}

@Composable
fun SortControls(
    onSliderValueChange: (Float) -> Unit,
    getOperationSize: () -> Int,
    onPrevStep: () -> Unit,
    onPlay: () -> Unit,
    onNextStep: () -> Unit,
    onResetClick: () -> Unit,
    step: Float,
    isPlaying: Boolean,
    setDelay: (Long) -> Unit,
    delay: Long,
    modifier: Modifier = Modifier,
) {
    val opSize = getOperationSize()
    val playButtonText = remember(isPlaying) { if (isPlaying) "PAUSE" else "Start Sorting" }

    val rOnSliderValueChange = remember { onSliderValueChange }
    val rOnPlay = remember { { onPlay() } }

    val stepText = remember(step) { "${step.toInt()}" }

    val sliderPosition by remember(opSize, step) {
        mutableFloatStateOf(
            if (opSize == 0) {
                0f
            } else {
                (step / opSize)
            }
        )
    }

    val iconButtonColors = IconButtonDefaults.filledIconButtonColors(

    )
    val pauseColors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    )



    Row {
        Slider(
            value = sliderPosition,
            onValueChange = rOnSliderValueChange,
            steps = opSize,
        )
    }
    Text(text = stepText, fontSize = 56.sp)

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
//            .border(1.dp, Color.Red)
            .wrapContentHeight()
//            .fillMaxWidth()
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
            var text ="Play"
//            var text by remember { mutableStateOf("") }


            val colors = if (isPlaying) pauseColors else iconButtonColors

            val icon = if (isPlaying) {
                text="Pause"
                R.drawable.ic_pause
            } else {
                text="Play"
                R.drawable.ic_play
            }

            Text(text)
            Spacer(Modifier.height(4.dp))
            IconButton(
                onClick = rOnPlay,
                colors =  colors,
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
//            Text("Next Step")
            }
        }
    }

    var delayText by remember(delay) { mutableStateOf(delay) }
    Row {
        TextField(
            value = "$delayText", onValueChange = {
                delayText = it.filter { it.isDigit() }.toLongOrNull() ?: 0
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(onClick = { setDelay(delayText) }) {
            Text(text = "SAVE DELAY")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SortInfoView(items: List<Item>) {

    val spacing = 4.dp
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val totalWidth = this.maxWidth
        val itemCount = items.size
        val availableWidth = totalWidth - (spacing * (itemCount - 1))
        val itemWidth by remember(items.size) {
            derivedStateOf {
                (availableWidth / itemCount.plus(1)).coerceIn(
                    4.dp,
                    40.dp
                )
            }
        }
        val rItems = remember(items) { items }

        val height = remember(rItems.size) { rItems.maxOfOrNull { it.value } ?: 100 }
        Column {
            // Gráfico dos Itens
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp * height)
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                items(rItems, key = { item -> item.id }) { item ->
                    BarItem(
                        value = item.value,
                        modifier = Modifier
                            .width(itemWidth)
                            .animateItemPlacement(),
                        barModifier = Modifier.width(itemWidth),
                        status = item.status
                    )
                }
            }

            // Índices abaixo das barras
            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                for (n in 0..rItems.lastIndex) {
                    Text(
                        text = "$n",
                        modifier = Modifier.width(itemWidth),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                    )
                }
            }
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
    Dialog(onDismissRequest = { onDismissRequest() }
    ) {
        Column(
            modifier = Modifier
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
                            Log.d("ALGO_NAMME_TEST", "CLICOU EM ${it.name}")
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
        content = content
    )
}

@Composable
fun DrawerContent(
    onAlgorithmChange: (SortAlgorithmName) -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet {
        SortAlgorithmName.entries.map {
            NavigationDrawerItem(label = { Text(it.name) }, selected = false,
                onClick = {
                    onAlgorithmChange(it)
                    closeDrawer()
                })

        }
//        NavigationDrawerItem(label = { Text("LALALA") }, selected = false, onClick = { })
//        NavigationDrawerItem(label = { Text("LALALA") }, selected = false, onClick = { })
//        NavigationDrawerItem(label = { Text("LALALA") }, selected = false, onClick = { })
    }
}

