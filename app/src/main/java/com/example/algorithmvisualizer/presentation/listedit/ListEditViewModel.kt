package com.example.algorithmvisualizer.presentation.listedit

import androidx.compose.runtime.Stable
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algorithmvisualizer.domain.repository.PreferencesRepository
import com.example.algorithmvisualizer.presentation.utils.KeyedValue
import com.example.algorithmvisualizer.presentation.utils.generateRandomValues
import com.example.algorithmvisualizer.presentation.utils.toKeyedValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
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

    private val state = MutableStateFlow(ListEditState())

    val listEditUiState = numbers.combine(state) { _, s -> s }
        .map { st ->
            ListEditUiState.Success(
                list = st.items,
                radioValue = st.radioValue,
                randomSize = st.randomSize
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ListEditUiState.Loading
        )


    init {
        numbers.onEach { ns ->
            state.update { st ->
                st.copy(
                    items = ns.map { it.toKeyedValue() }
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun onAddItem(value: Int) {
        state.update { st ->
            st.copy(
                items = ((st.items) + value.toKeyedValue())
                    .toMutableList()
            )
        }
    }

    private fun onDeleteItem(key: String) {
        state.update { st ->
            val indexToRemove = st.items.indexOfFirst { it.key == key }
            if (indexToRemove < 0) return

            val newItems = st.items.toMutableList().apply {
                removeAt(indexToRemove)
            }

            st.copy(items = newItems)
        }

    }

    private fun onRandomListItems(size: Int) {
        state.update { st ->
            val newValues = generateRandomValues(size.coerceAtLeast(1))
                .map { it.toKeyedValue() }
            st.copy(items = newValues)
        }

    }

    private suspend fun onSaveClick() {
        withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
            val values = state.value.items.map { it.value }
            preferencesRepository.saveNumbersList(values)
        }

    }

    fun onEvent(event: ListEditUiEvent) {
        viewModelScope.launch {
            when (event) {
                is ListEditUiEvent.OnAddItemClick -> {
                    val text = event.valueProvider()
                    if (text.isDigitsOnly() && text.isNotEmpty()) {
                        onAddItem(text.toInt())
                    }
                }

                is ListEditUiEvent.OnItemClick -> {
                    val key = event.keyProvider()
                    onDeleteItem(key)
                }

                is ListEditUiEvent.OnRandomListItemsClick -> {
                    onRandomListItems(event.size)
                }

                ListEditUiEvent.OnSaveClick -> {
                    onSaveClick()
                }
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

sealed class ListEditUiEvent {
    data object OnSaveClick : ListEditUiEvent()
    data class OnRandomListItemsClick(val size: Int) : ListEditUiEvent()
    data class OnAddItemClick(val valueProvider: () -> String) : ListEditUiEvent()
    data class OnItemClick(val keyProvider: () -> String) : ListEditUiEvent()
}


@Stable
data class ListEditState(
    val items: List<KeyedValue<String, Int>> = emptyList(),
    val randomSize: String = "",
    val radioValue: Int = 1,
)