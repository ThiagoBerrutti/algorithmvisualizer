package com.example.algorithmvisualizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.algorithmvisualizer.data.repository.PreferencesRepositoryImpl
import com.example.algorithmvisualizer.domain.repository.PreferencesRepository
import com.example.algorithmvisualizer.presentation.ui.AlgoVisApp
import com.example.algorithmvisualizer.presentation.ui.rememberAppState
import com.example.algorithmvisualizer.presentation.ui.theme.AlgorithmVisualizerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity(

) : ComponentActivity() {
    @Inject
    lateinit var userDataRepository: PreferencesRepository

    private val viewModel: MainActivityViewModel by viewModels()

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)
        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }

        enableEdgeToEdge()
        setContent {
            AlgorithmVisualizerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    ) { innerPadding ->
                    Box(
                        Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                            .statusBarsPadding()
                            .fillMaxSize()
                    ) {

                        val navController = rememberNavController()

                        val appState =
                            rememberAppState(
                                navController = navController
                            )

//                        val appState2 = AlgoVisAppState(navController)
                        AlgoVisApp(appState = appState)
                    }

                }
            }
        }
    }
}


