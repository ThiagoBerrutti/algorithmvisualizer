package com.example.algorithmvisualizer.presentation.sort

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.algorithmvisualizer.domain.model.SortAlgorithmName
import com.example.algorithmvisualizer.domain.model.quicksort.QuickSortAction
import com.example.algorithmvisualizer.domain.model.quicksort.QuickSortOperation
import com.example.algorithmvisualizer.presentation.components.BarChart
import com.example.algorithmvisualizer.presentation.sort.components.AlgorithmNameDialog
import com.example.algorithmvisualizer.presentation.sort.components.SortControls
import com.example.algorithmvisualizer.presentation.sort.components.SortOperationInfo
import com.example.algorithmvisualizer.presentation.sort.components.SortSteps
import com.example.algorithmvisualizer.presentation.sort.components.StepDialog
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
                        onDismissRequest = onStepDialogDismiss
                    )
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


@Preview(
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





