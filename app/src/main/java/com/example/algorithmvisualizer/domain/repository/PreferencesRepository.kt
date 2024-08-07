package com.example.algorithmvisualizer.domain.repository

import com.example.algorithmvisualizer.data.UserData
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun saveDelay(delay:Long)
    suspend fun saveNumbersList(numbersList: List<Int>)
    suspend fun saveShowIndices(show: Boolean)
    suspend fun saveShowValues(show: Boolean)
    suspend fun saveSortInfoVisibility(show: Boolean)

    val userData: Flow<UserData>
}