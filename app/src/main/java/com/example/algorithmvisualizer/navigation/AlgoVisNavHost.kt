package com.example.algorithmvisualizer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.navigation.compose.NavHost
import com.example.algorithmvisualizer.presentation.listedit.navigation.LIST_EDIT_SCREEN_ROUTE
import com.example.algorithmvisualizer.presentation.listedit.navigation.listEditScreen
import com.example.algorithmvisualizer.presentation.settings.navigation.settingsScreen
import com.example.algorithmvisualizer.presentation.sort.navigation.SORT_SCREEN_ROUTE
import com.example.algorithmvisualizer.presentation.sort.navigation.sortScreen
import com.example.algorithmvisualizer.presentation.ui.AlgoVisAppState

@Stable
data class AlgoVisNavHostState(
    val appState: AlgoVisAppState,
//    val startDestination: String = LIST_EDIT_SCREEN_ROUTE,
    val startDestination: String = SORT_SCREEN_ROUTE,
)

@Composable
fun AlgoVisNavHost(
    state: AlgoVisNavHostState,
) {
    NavHost(
        navController = state.appState.navController,
        startDestination = state.startDestination,
    ) {
        sortScreen()
        listEditScreen(
            onBackClick = { state.appState.navController.navigateUp() },
        )
        settingsScreen(onBackClick = { state.appState.navController.navigateUp() },)
    }
}


