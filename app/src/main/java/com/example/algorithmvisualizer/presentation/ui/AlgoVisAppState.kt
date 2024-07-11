package com.example.algorithmvisualizer.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.example.algorithmvisualizer.data.repository.PreferencesRepository
import kotlinx.coroutines.CoroutineScope

@Stable
class AlgoVisAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
    val userDataRepository: PreferencesRepository
) {}

@Composable
fun rememberAppState(
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    userDataRepository: PreferencesRepository
): AlgoVisAppState {
    return remember(
        navController,
        coroutineScope,
        userDataRepository
    ) {
        AlgoVisAppState(
            navController,
            coroutineScope,
            userDataRepository
        )
    }
}