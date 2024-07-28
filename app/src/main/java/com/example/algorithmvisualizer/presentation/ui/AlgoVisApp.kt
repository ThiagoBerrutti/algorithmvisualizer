package com.example.algorithmvisualizer.presentation.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.algorithmvisualizer.R
import com.example.algorithmvisualizer.navigation.AlgoVisNavHost
import com.example.algorithmvisualizer.navigation.AlgoVisNavHostState
import com.example.algorithmvisualizer.navigation.TopDestination

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlgoVisApp(appState: AlgoVisAppState) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            NavigationBar {
                val c by appState.navController.currentBackStackEntryAsState()

                TopDestination.entries.map { destination ->
                    val selected = c?.destination.isTopLevelDestinationInHierarchy(destination)

                    NavigationBarItem(
                        label = { Text(destination.label) },
                        selected = selected,
                        onClick = { appState.navigateToTopLevelDestination(destination) },
                        icon = {
                            Icon(
                                painter = painterResource(id = destination.icon),
                                contentDescription = null
                            )
                        },
                    )
                }
            }

        }) {padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .consumeWindowInsets(padding)
                .fillMaxSize()
                .border(2.dp, Color.Blue)
        ){
            val state = remember {AlgoVisNavHostState(appState)}
            AlgoVisNavHost(state)
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController, modifier: Modifier = Modifier) {


//        NavigationBarItem(
//            label = { Text("Charts") },
//            selected = currentRoute == SORT_SCREEN_ROUTE,
//            onClick = {
//                navController.navigate(
//                    route = SORT_SCREEN_ROUTE,
////                    navOptions = navOptions {
////                        popUpTo(SORT_SCREEN_ROUTE) {
////                            saveState = true
////                        }
//////                        launchSingleTop = true
//////                        restoreState = true
////                    }
//                ) },
//            icon = {
//                Icon(painterResource(id = R.drawable.ic_bar_chart_2), "", modifier = iconModifier)
//            },
//            modifier = buttonModifier
//        )
//
//        NavigationBarItem(
//            label = { Text("Config") },
//            selected = currentRoute == LIST_EDIT_SCREEN_ROUTE,
//            onClick = {
//                navController.navigate(
//                    route = LIST_EDIT_SCREEN_ROUTE,
////                    navOptions = navOptions {
////                        popUpTo(SORT_SCREEN_ROUTE) {
////                            saveState = true
////                        }
////                        launchSingleTop = true
////                        restoreState = true
////                    }
//                )
//            },
//            icon = {
//                Icon(painterResource(id = R.drawable.ic_settings_2), "", modifier = iconModifier)
//            }, modifier = buttonModifier
//        )
//
//        NavigationBarItem(
//            label = { Text("Settings") },
//            selected = currentRoute == SETTINGS_ROUTE,
//            onClick = {
//                navController.navigate(
//                    route = SETTINGS_ROUTE,
////                    navOptions = navOptions {
////                        popUpTo(SORT_SCREEN_ROUTE) {
////                            saveState = true
////                        }
////                        launchSingleTop = true
////                        restoreState = true
////                    }
//                )
//            },
//            icon = {
//                Icon(painterResource(id = R.drawable.ic_settings), "", modifier = iconModifier)
//            }, modifier = buttonModifier
//        )


}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

