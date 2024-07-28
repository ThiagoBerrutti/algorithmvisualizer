package com.example.algorithmvisualizer.presentation.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.example.algorithmvisualizer.data.repository.PreferencesRepository
import com.example.algorithmvisualizer.navigation.TopDestination
import com.example.algorithmvisualizer.navigation.TopDestination.LIST_EDIT_SCREEN_ROUTE
import com.example.algorithmvisualizer.navigation.TopDestination.SETTINGS_ROUTE
import com.example.algorithmvisualizer.navigation.TopDestination.SORT_SCREEN_ROUTE
import com.example.algorithmvisualizer.presentation.listedit.navigation.navigateToListEdit
import com.example.algorithmvisualizer.presentation.settings.navigation.navigateToSettings
import com.example.algorithmvisualizer.presentation.sort.navigation.navigateToSort
import kotlinx.coroutines.CoroutineScope

@Stable
class AlgoVisAppState(
    val navController: NavHostController,
) {
//    val currentDestination: NavDestination?
//        @Composable get() = navController
//            .currentBackStackEntryAsState().value?.destination

    fun navigateToTopLevelDestination(topDestination: TopDestination) {
        val d = navController.graph.findStartDestination().route
//        trace("Navigation: ${topDestination.name}") {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
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
    navController: NavHostController
): AlgoVisAppState {
    return remember(
        navController
    ) {
        AlgoVisAppState(
            navController,
        )
    }
}