package com.example.algorithmvisualizer.data.repository

//import kotlinx.coroutines.flow.combine
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.algorithmvisualizer.data.PreferencesKeys
import com.example.algorithmvisualizer.data.UserData
import com.example.algorithmvisualizer.presentation.utils.generateRandomValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesRepository(val context: Context) {
    private val data = context.dataStore.data
        .catch { emit(emptyPreferences()) }


    private val delay: Flow<Long?> =
        context.dataStore.data.map { it[PreferencesKeys.DELAY_MS] }

    private val showIndices = context.dataStore.data.map{it[PreferencesKeys.SHOW_INDICES] ?:false}
    private val showValues = context.dataStore.data.map{it[PreferencesKeys.SHOW_VALUES] ?: false}


    @OptIn(ExperimentalCoroutinesApi::class)
    private val delayIfNull: Flow<Long> = delay
        .flatMapLatest {
            flowOf(it ?: 50)
        }

    val isSortInfoVisible: Flow<Boolean?> = context.dataStore.data
        .map { it[PreferencesKeys.SHOW_INFO] }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isSortInfoVisibleIfNull = isSortInfoVisible.flatMapLatest {
        flowOf(it ?: true)
    }

    suspend fun saveSortInfoVisibility(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_INFO] = show
        }
    }


    suspend fun saveNumbersList(numbersList: List<Int>) {
        val jsonString = Json.encodeToString(numbersList)
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NUMBERS_LIST] = jsonString
        }
    }


    private val numberList: Flow<List<Int>> = context.dataStore.data.map { preferences ->
        val jsonString = preferences[PreferencesKeys.NUMBERS_LIST] ?: "[]"
        Json.decodeFromString<List<Int>>(jsonString)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val numberListIfNull = numberList.flatMapLatest { list ->
        if (list.isEmpty()) {
            saveRandomValuesAndGetFlow()
        } else {
            flowOf(list)
        }
    }

    val userData = combine(
        numberListIfNull,
        delayIfNull,
        isSortInfoVisibleIfNull,
        showIndices,
        showValues,
        ::UserData
    )
        .distinctUntilChanged()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            delay.take(1).onEach {
                if (it == null) {
                    saveDelay(50)
                }
            }
        }
    }

    suspend fun saveDelay(delay: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DELAY_MS] = delay
        }
    }

    private fun saveRandomValuesAndGetFlow(): Flow<List<Int>> = flow {
        val randomValues = generateRandomValues()
        saveNumbersList(randomValues)
        emit(randomValues)
    }

    suspend fun saveShowIndices(show:Boolean){
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_INDICES] = show
        }
    }

    suspend fun saveShowValues(show:Boolean){
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_VALUES] = show
        }
    }




}

