package com.example.algorithmvisualizer.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.algorithmvisualizer.data.PreferencesKeys
import com.example.algorithmvisualizer.presentation.utils.generateRandomValues
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesRepository(val context: Context) {
    private val data = context.dataStore.data
        .catch { emit(emptyPreferences()) }

    suspend fun saveDelay(delay: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DELAY_MS] = delay
        }
    }

//    private fun tryGetDelay() {
//        context.dataStore.data
//            .catch { emit(emptyPreferences()) }
//            .map { it[PreferencesKeys.DELAY_MS] }
//            .map{
//                it ?:
//
//                saveNumbersList()
//            }
//    }
     val delay: Flow<Long?> =
        context.dataStore.data
            .map { it[PreferencesKeys.DELAY_MS] }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val delayIfNull: Flow<Long> =delay
            .flatMapLatest {
                flowOf(it ?: 50)
            }




    suspend fun saveNumbersList(numbersList: List<Int>) {
        val jsonString = Json.encodeToString(numbersList)
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NUMBERS_LIST] = jsonString
        }
    }

    private fun saveRandomValuesAndGetFlow(): Flow<List<Int>> = flow {
        val randomValues = generateRandomValues()
        saveNumbersList(randomValues)
        emit(randomValues)
    }

    private val numberList: Flow<List<Int>> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[PreferencesKeys.NUMBERS_LIST] ?: "[]"
       Json.decodeFromString<List<Int>>(jsonString)
    }

//    private val numberList = flowOf(listOf(10,10,10,10,10,10,10))

    private val numberListIfNull = numberList.flatMapLatest { list ->
        if (list.isEmpty()) {
            saveRandomValuesAndGetFlow()
        } else {
            flowOf(list)
        }
    }


    val userData = numberListIfNull.combine(delayIfNull){ list, delay ->
        UserData(list, delay)
    }
}

data class UserData(
    val numbersList: List<Int>,
    val delay:Long
)