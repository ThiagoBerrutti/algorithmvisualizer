package com.example.algorithmvisualizer.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.algorithmvisualizer.navigation.AlgoVisNavHost
import com.example.algorithmvisualizer.navigation.AlgoVisNavHostState
import com.example.algorithmvisualizer.navigation.TopDestination

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlgoVisApp(appState: AlgoVisAppState) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier
            .fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            AppBottomNavigationBar(appState = appState)

        }) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal,
                    )
                )
                .fillMaxSize()
//                .border(2.dp, Color.Blue)
        ) {
            val state = remember { AlgoVisNavHostState(appState) }
            AlgoVisNavHost(state)
        }
    }
}

@Composable
fun AppBottomNavigationBar(appState: AlgoVisAppState, modifier: Modifier = Modifier) {
    NavigationBar(modifier = modifier) {
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
}

@Preview
@Composable
private fun AppBottomNavigationBarPreview() {
    val appState = AlgoVisAppState(rememberNavController())
    AppBottomNavigationBar(appState)
}


private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

