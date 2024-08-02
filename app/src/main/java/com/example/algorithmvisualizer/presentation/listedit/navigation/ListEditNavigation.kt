package com.example.algorithmvisualizer.presentation.listedit.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.algorithmvisualizer.presentation.listedit.ListEditRoute


const val LIST_EDIT_SCREEN_ROUTE = "list_edit_screen_route"

fun NavController.navigateToListEdit(navOptions: NavOptions) =
    navigate(LIST_EDIT_SCREEN_ROUTE, navOptions)

fun NavGraphBuilder.listEditScreen(onBackClick: () -> Unit) {
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
        ListEditRoute(
            onBackClick = onBackClick,
        )
    }
}