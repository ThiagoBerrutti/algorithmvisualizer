package com.example.algorithmvisualizer.presentation.settings.navigation

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.algorithmvisualizer.presentation.settings.SettingsRoute

const val SETTINGS_ROUTE = "settings_route"

fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(SETTINGS_ROUTE, navOptions)

fun NavGraphBuilder.settingsScreen(onBackClick: () ->Unit) =
    composable(SETTINGS_ROUTE) {
        SettingsRoute(onBackClick = onBackClick)
    }