package com.example.algorithmvisualizer.presentation.sort.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.algorithmvisualizer.presentation.sort.SortRoute
import com.example.algorithmvisualizer.presentation.sort.SortScreen

const val SORT_SCREEN_ROUTE = "sort_screen_route"

fun NavGraphBuilder.sortScreen(){
    composable(route = SORT_SCREEN_ROUTE){
        SortRoute()
    }
}