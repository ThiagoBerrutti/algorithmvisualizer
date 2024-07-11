package com.example.algorithmvisualizer.data

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val DELAY_MS = longPreferencesKey("delay_ms")
    val NUMBERS_LIST = stringPreferencesKey("numbers_list")


}