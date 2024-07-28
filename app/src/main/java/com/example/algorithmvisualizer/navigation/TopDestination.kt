package com.example.algorithmvisualizer.navigation

import androidx.annotation.DrawableRes
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.algorithmvisualizer.R

enum class TopDestination(val route: String, val label: String, @DrawableRes val icon: Int) {
    SORT_SCREEN_ROUTE(
        route = "SORT_SCREEN_ROUTE",
        label = "Charts",
        icon = R.drawable.ic_bar_chart_2
    ),
    LIST_EDIT_SCREEN_ROUTE(
        route = "LIST_EDIT_SCREEN_ROUTE",
        label = "Config",
        icon = R.drawable.ic_settings_2
    ),

    SETTINGS_ROUTE(
        route = "SETTINGS_ROUTE",
        label = "Settings",
        icon = R.drawable.ic_settings
    )

}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false