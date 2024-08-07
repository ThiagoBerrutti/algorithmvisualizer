package com.example.algorithmvisualizer.presentation.settings

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algorithmvisualizer.data.repository.PreferencesRepositoryImpl
import com.example.algorithmvisualizer.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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


    private var stt = mutableStateOf(SettingsState())


    val uiState: StateFlow<SettingsUiState> = repo.userData
        .onEach {
            stt.value = stt.value.copy(
                delay = "${it.delay}",
                showInfo = it.isSortInfoVisible,
                showIndices = it.showIndices,
                showValues = it.showValues
            )
        }
        .map { data ->
            SettingsUiState.Success(
                delay = "${data.delay}",
                isSortInfoVisible = data.isSortInfoVisible,
                showIndices = data.showIndices,
                showValues = data.showValues,
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState.Loading)


    private val ioScope = CoroutineScope(Dispatchers.IO)


    fun onEvent(event: SettingsUiEvent) {
        viewModelScope.launch {
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

                SettingsUiEvent.OnSaveClick -> {
                    withContext(ioScope.coroutineContext) {
                        val value = stt.value.delay.let { d ->
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
            }
        }
    }
}

sealed class SettingsUiEvent {
    data class SaveDelayClick(val delay: String) : SettingsUiEvent()
    data object OnSaveClick : SettingsUiEvent()
    data object ShowIndicesClick : SettingsUiEvent()
    data object ShowInfoClick : SettingsUiEvent()
    data object ShowValuesClick : SettingsUiEvent()
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