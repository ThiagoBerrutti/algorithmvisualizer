package com.example.algorithmvisualizer.presentation.settings.navigation

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.algorithmvisualizer.data.repository.PreferencesRepository
import com.example.algorithmvisualizer.presentation.listedit.navigation.LIST_EDIT_SCREEN_ROUTE
import com.example.algorithmvisualizer.presentation.settings.SettingsRoute
import com.example.algorithmvisualizer.presentation.settings.SettingsScreen
import com.example.algorithmvisualizer.presentation.ui.AlgoVisAppState

const val SETTINGS_ROUTE = "settings_route"

fun NavController.navigateToSettings(navOptions: NavOptions) = navigate(SETTINGS_ROUTE, navOptions)

fun NavGraphBuilder.settingsScreen() =
    composable(SETTINGS_ROUTE){
        LaunchedEffect(it.id) {
            Log.d("ROUTE_COMPOSE_TEST","settings screen")
        }
        SettingsRoute()
//        SettingsScreen()
    }