package com.example.algorithmvisualizer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.algorithmvisualizer.presentation.sort.navigation.SORT_SCREEN_ROUTE
import com.example.algorithmvisualizer.presentation.sort.navigation.sortScreen
import com.example.algorithmvisualizer.presentation.ui.AlgoVisAppState

@Composable
fun AlgoVisNavHost(
    appState: AlgoVisAppState,
    modifier: Modifier = Modifier,
    startDestination: String = SORT_SCREEN_ROUTE
) {

    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
        ){
            sortScreen()
    }
}