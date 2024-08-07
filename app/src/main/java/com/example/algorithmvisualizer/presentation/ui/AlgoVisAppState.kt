package com.example.algorithmvisualizer.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.example.algorithmvisualizer.navigation.TopDestination
import com.example.algorithmvisualizer.navigation.TopDestination.LIST_EDIT_SCREEN_ROUTE
import com.example.algorithmvisualizer.navigation.TopDestination.SETTINGS_ROUTE
import com.example.algorithmvisualizer.navigation.TopDestination.SORT_SCREEN_ROUTE
import com.example.algorithmvisualizer.presentation.listedit.navigation.navigateToListEdit
import com.example.algorithmvisualizer.presentation.settings.navigation.navigateToSettings
import com.example.algorithmvisualizer.presentation.sort.navigation.navigateToSort

@Stable
class AlgoVisAppState(
    val navController: NavHostController,
) {
    fun navigateToTopLevelDestination(topDestination: TopDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (topDestination) {
            SORT_SCREEN_ROUTE -> navController.navigateToSort(topLevelNavOptions)
            LIST_EDIT_SCREEN_ROUTE -> navController.navigateToListEdit(topLevelNavOptions)
            SETTINGS_ROUTE -> navController.navigateToSettings(topLevelNavOptions)
        }
    }


}


@Composable
fun rememberAppState(
    navController: NavHostController,
): AlgoVisAppState {
    return remember(
        navController
    ) {
        AlgoVisAppState(
            navController,
        )
    }
}