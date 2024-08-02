package com.example.algorithmvisualizer.presentation.sort.navigation

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.algorithmvisualizer.presentation.sort.SortRoute

const val SORT_SCREEN_ROUTE = "sort_screen_route"

fun NavController.navigateToSort(navOptions: NavOptions) {
    if (this.currentDestination?.route != SORT_SCREEN_ROUTE) {
        navigate(SORT_SCREEN_ROUTE, navOptions)
    }
}


fun NavGraphBuilder.sortScreen() {
    composable(route = SORT_SCREEN_ROUTE
    ) {
        SortRoute()
    }
}