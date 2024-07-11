package com.example.algorithmvisualizer

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.twotone.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.algorithmvisualizer.data.repository.PreferencesRepository
import com.example.algorithmvisualizer.presentation.ui.AlgoVisApp
import com.example.algorithmvisualizer.presentation.ui.rememberAppState
import com.example.algorithmvisualizer.presentation.ui.theme.AlgorithmVisualizerTheme
import com.example.algorithmvisualizer.presentation.utils.generateItems
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var userDataRepository: PreferencesRepository

    val viewModel: MainActivityViewModel by viewModels()

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
                Scaffold(modifier = Modifier.fillMaxSize(),

                    ) { innerPadding ->
                    Box(
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
//                            .border(5.dp, Color.Red)
                    ) {
                        val items by remember {
                            derivedStateOf {
                                generateItems(180)
//                                generateStaticItems(80)
                            }
                        }
                        val navController = rememberNavController()
                        val scope = rememberCoroutineScope()

                        LaunchedEffect(key1 = 1) {
                            val userData = userDataRepository.userData.firstOrNull()
                            Log.d("TEST_TEST", userData!!::class.simpleName.toString())
                        }

                        val appState =
                            rememberAppState(
                                navController = navController,
                                coroutineScope = scope,
                                userDataRepository = userDataRepository
                                )
                        AlgoVisApp(appState = appState)
                    }

                }
            }
        }
    }
}


