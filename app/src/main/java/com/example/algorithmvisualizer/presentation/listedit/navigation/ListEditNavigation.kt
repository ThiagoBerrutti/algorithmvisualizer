package com.example.algorithmvisualizer.presentation.listedit.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.algorithmvisualizer.presentation.listedit.ListEditRoute
import com.example.algorithmvisualizer.presentation.ui.AlgoVisAppState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


const val LIST_EDIT_SCREEN_ROUTE = "list_edit_screen_route"

fun NavController.navigateToListEdit(navOptions: NavOptions) = navigate(LIST_EDIT_SCREEN_ROUTE, navOptions)

fun NavGraphBuilder.listEditScreen(onBackClick:() ->Unit, ) {
    composable(
        route = LIST_EDIT_SCREEN_ROUTE,
//        enterTransition = {
//            slideIntoContainer(
//                towards = AnimatedContentTransitionScope.SlideDirection.Left,
//                animationSpec=tween(300)
//            )
//        },
//        exitTransition = {
//            slideOutOfContainer(
//                towards = AnimatedContentTransitionScope.SlideDirection.Right,
//                animationSpec=tween(300)
//            )
//        }
    ) {
        val rOnBackClick = remember {onBackClick}
        ListEditRoute(
            onBackClick = onBackClick,
        )
    }
}