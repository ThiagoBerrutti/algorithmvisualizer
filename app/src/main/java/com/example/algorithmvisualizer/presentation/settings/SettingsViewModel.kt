package com.example.algorithmvisualizer.presentation.settings

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algorithmvisualizer.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Stable
data class SettingsState(
    val delay: String = "",
    val showInfo: Boolean = true,
    val showIndices: Boolean = true,
    val showValues: Boolean = true,
)


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: PreferencesRepository,
) : ViewModel() {

//    private var _state = MutableStateFlow(SettingsState())
    private var stt = mutableStateOf(SettingsState())

//    var f = MutableStateFlow(SettingsState())
//    private val state: StateFlow<SettingsState> = _state

//    private val userData = repo.userData

    val uiState: StateFlow<SettingsUiState> = repo.userData
        .onEach { stt.value=stt.value.copy(
            delay = "${it.delay}",
            showInfo = it.isSortInfoVisible,
            showIndices = it.showIndices,
            showValues = it.showValues
        ) }
        .map{ data ->
        SettingsUiState.Success(
            delay = "${data.delay}",
            isSortInfoVisible = data.isSortInfoVisible,
            showIndices = data.showIndices,
            showValues = data.showValues,
        )
    }
//        .onEach { Log.d("UISTATE_USERDATA_TEST", "#2: $it") ; delay(100)}//
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState.Loading)


    private val ioScope = CoroutineScope(Dispatchers.IO)


    fun onEvent(event: SettingsUiEvent) {
        viewModelScope.launch {
//            Log.d("SETTINGS_VM_EVENT_TEST", "$event")
            delay(1000)
            when (event) {
                is SettingsUiEvent.SaveDelayClick -> {
                    withContext(ioScope.coroutineContext) {
                        val value = event.delay.let { d ->
                            if (d.isNotBlank() && d.isDigitsOnly()) {
                                d.toLong()
                            } else {
                                stt.value = stt.value.copy(delay = stt.value.delay)
                                stt.value.delay.toLongOrNull() ?: 69
                            }
                        }
                        repo.saveDelay(value)
                    }
                }

                is SettingsUiEvent.ShowIndicesClick -> {
                    withContext(ioScope.coroutineContext) {
                        val value = stt.value.showIndices
                        repo.saveShowIndices(!value)
                    }
                }

                is SettingsUiEvent.ShowInfoClick -> {
                    withContext(ioScope.coroutineContext) {
                        val value = stt.value.showInfo
                        repo.saveSortInfoVisibility(!value)
                    }
                }

                is SettingsUiEvent.ShowValuesClick -> {
                    withContext(ioScope.coroutineContext) {
                        val value = stt.value.showValues
                        repo.saveShowValues(!value)
                    }
                }

                is SettingsUiEvent.OnDelayTextChange -> {
                    withContext(ioScope.coroutineContext) {
                        event.text.let { txt ->
                            if (txt.isNotBlank() && txt.isDigitsOnly()) {
                                stt.value = stt.value.copy(delay = txt)
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class SettingsUiEvent {
    data object ShowInfoClick : SettingsUiEvent()
    data object ShowIndicesClick : SettingsUiEvent()
    data object ShowValuesClick : SettingsUiEvent()
    data class SaveDelayClick(val delay: String) : SettingsUiEvent()
    data class OnDelayTextChange(val text: String) : SettingsUiEvent()

}

sealed class SettingsUiState {
    data object Loading : SettingsUiState()
    data class Success(
        val delay: String,
        val isSortInfoVisible: Boolean,
        val showIndices: Boolean,
        val showValues: Boolean,
    ) : SettingsUiState()
}