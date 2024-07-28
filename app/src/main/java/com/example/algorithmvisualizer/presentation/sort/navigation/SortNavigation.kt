package com.example.algorithmvisualizer.presentation.sort.navigation

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.algorithmvisualizer.presentation.settings.navigation.SETTINGS_ROUTE
import com.example.algorithmvisualizer.presentation.sort.SortRoute
import com.example.algorithmvisualizer.presentation.sort.SortScreen
import com.example.algorithmvisualizer.presentation.sort.SortViewModel

const val SORT_SCREEN_ROUTE = "sort_screen_route"

fun NavController.navigateToSort(navOptions: NavOptions) {
    if (this.currentDestination?.route != SORT_SCREEN_ROUTE) {
        navigate(SORT_SCREEN_ROUTE, navOptions)
    }
}


fun NavGraphBuilder.sortScreen(){
    composable(
        route = SORT_SCREEN_ROUTE){
//        var isFirstTime by remember { mutableStateOf(true) }
        LaunchedEffect(null) {
            Log.d("COMP_TEST", "")
        }
//        val vm: SortViewModel = hiltViewModel()
//        val parentEntry = remember {navController.getBackStackEntry()}
//        val vm: SortViewModel = hiltViewModel()
//        val vm2 = remember {vm}
//        if (isFirstTime) {
            SortRoute()
//            isFirstTime = false
//        }


    }
}