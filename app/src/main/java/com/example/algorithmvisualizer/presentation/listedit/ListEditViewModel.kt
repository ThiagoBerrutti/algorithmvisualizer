package com.example.algorithmvisualizer.presentation.listedit

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algorithmvisualizer.data.repository.PreferencesRepository
import com.example.algorithmvisualizer.presentation.utils.KeyedValue
import com.example.algorithmvisualizer.presentation.utils.generateRandomValues
import com.example.algorithmvisualizer.presentation.utils.toKeyedValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ListEditViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {
    private val userData = preferencesRepository.userData
    private val numbers = userData.map { it.numbersList }

    private val state =  MutableStateFlow(ListEditState())

    val listEditUiState= numbers.combine( state){n,s-> s }

        .map { st ->
        ListEditUiState.Success(
            list=st.items,
            radioValue = st.radioValue,
            randomSize = st.randomSize
        )
     }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListEditUiState.Loading)  // userData.map { it.numbersList }


    init {
        numbers.onEach { ns ->
            state.update { st ->
                st.copy(
                    items = ns.map { it.toKeyedValue() }
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAddItem(value: Int) {
        viewModelScope.launch {
            state.update { st ->
              st.copy(items = ((st.items) + value.toKeyedValue())
                  .toMutableList())
            }
        }
    }

    fun onDeleteItem(key: String) {
        viewModelScope.launch {
            state.update {st ->
                val indexToRemove = st.items.indexOfFirst{ it.key == key} ?: -1
                if (indexToRemove <0) return@launch

                val newItems = st.items.toMutableList().apply{
                    removeAt(indexToRemove)
                }

                st.copy(items = newItems)
            }
        }
    }

    fun onRandomListItems(size: Int) {
        viewModelScope.launch {
            state.update {st ->
                val newValues = generateRandomValues(size.coerceAtLeast(1))
                    .map{it.toKeyedValue()}
                   st.copy(items = newValues)
            }
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                val values = state.value.items.map {it.value}
                preferencesRepository.saveNumbersList(values)
                }
        }
    }
}

sealed class ListEditUiState {
    data object Loading : ListEditUiState()
    data class Success(
        val list: List<KeyedValue<String, Int>> = emptyList(),
        val randomSize: String = "",
        val radioValue: Int = 1,
    ) : ListEditUiState()
}

@Stable
data class ListEditState(
    val items:List<KeyedValue<String,Int>> = emptyList(),
    val randomSize:String = "",
    val radioValue: Int = 1

)