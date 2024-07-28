package com.example.algorithmvisualizer.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val DELAY_MS = longPreferencesKey("delay_ms")
    val NUMBERS_LIST = stringPreferencesKey("numbers_list")
    val SHOW_INFO = booleanPreferencesKey("show_info")
    val SHOW_INDICES = booleanPreferencesKey("show_indices")
    val SHOW_VALUES = booleanPreferencesKey("show_values")



}