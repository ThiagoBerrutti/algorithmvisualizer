package com.example.algorithmvisualizer.presentation.sort

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algorithmvisualizer.domain.model.ISortOperation
import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.SortAlgorithmName
import com.example.algorithmvisualizer.domain.model.bubblesort.BubbleSortAlgorithm
import com.example.algorithmvisualizer.domain.model.quicksort.QuickSortAlgorithm
import com.example.algorithmvisualizer.domain.repository.PreferencesRepository
import com.example.algorithmvisualizer.domain.usecase.GetNextSortOperationUseCase
import com.example.algorithmvisualizer.domain.usecase.GetPreviousSortOperationUseCase
import com.example.algorithmvisualizer.domain.usecase.SetSortStepUseCase
import com.example.algorithmvisualizer.presentation.utils.generateItemsFrom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt


data class ItemList(
    val items: List<Item>,
    val id: String,
)

sealed class SortScreenUiState {
    data object Loading : SortScreenUiState()
    data class Completed(
        val items: ItemList,
        val isSortInfoVisible: Boolean,
        val showIndices: Boolean,
        val showValues: Boolean,
        val currentStep: Int,
        val currentOperation: ISortOperation?,
    ) : SortScreenUiState()
}

@HiltViewModel
class SortViewModel @Inject constructor(
    preferencesRepo: PreferencesRepository,
) : ViewModel() {
    private val defaultScope = CoroutineScope(Dispatchers.Default)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val _initialItems: MutableStateFlow<List<Item>?> = MutableStateFlow(null)

    val algorithmName = MutableStateFlow(SortAlgorithmName.QuickSort)
    private val algorithmUpdates = MutableSharedFlow<SortAlgorithmName>()
    private val _algorithm = algorithmUpdates
        .onStart { emit(algorithmName.value) }
        .onEach { alg -> algorithmName.update { alg } }
        .combine(_initialItems.filterNotNull()) { a, i -> Pair(a, i) }
        .map { pair ->
            val (algName, items) = pair
            when (algName) {
                SortAlgorithmName.BubbleSort -> BubbleSortAlgorithm(items)
                SortAlgorithmName.QuickSort -> QuickSortAlgorithm(items)
            }
        }


    private val sortIterator = _algorithm
        .map { algorithm -> algorithm.sort() }
        .stateIn(ioScope, SharingStarted.WhileSubscribed(5_000), null)

    private val getNextSortOperationUseCase = GetNextSortOperationUseCase()
    private val getPreviousSortOperationUseCase = GetPreviousSortOperationUseCase()
    private val setSortStepUseCase = SetSortStepUseCase()

    private val _currentState = MutableStateFlow<List<Item>?>(null)
    private val _currentStep = MutableStateFlow(0)
    private val _currentOperation = MutableStateFlow<ISortOperation?>(null)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private var playJob: Job? = null
    private var setStepJob: Job? = null
    private var nextJob: Job? = null
    private var prevJob: Job? = null

    private val userData = preferencesRepo.userData
    private val delay = userData.map { it.delay }
    private val itemList = userData.map { it.numbersList }
        .distinctUntilChanged()
        .map { ItemList(emptyList(), "${it.hashCode()}") }
        .combine(_currentStep) { list, _ -> list }
        .combine(sortIterator.filterNotNull()) { list, iterator ->
            list.copy(items = iterator.getCurrentState())
        }

    private val settings = userData.map {
        Triple(
            it.isSortInfoVisible,
            it.showIndices,
            it.showValues,
        )
    }

    val uiState: StateFlow<SortScreenUiState> = combine(
        settings,
        sortIterator.filterNotNull(),
        _currentStep,
        _currentOperation,
        itemList
    ) { settings, _, step, operation, itemList ->
        SortScreenUiState.Completed(
            items = itemList,
            isSortInfoVisible = settings.first,
            showIndices = settings.second,
            showValues = settings.third,
            currentStep = step,
            currentOperation = operation
        )
    }
        .stateIn(defaultScope, SharingStarted.WhileSubscribed(5_000), SortScreenUiState.Loading)

    init {
        viewModelScope.launch {//
            algorithmUpdates.emit(SortAlgorithmName.QuickSort)

            // Atualiza os itens quando houver uma mudança nos itens salvos
            userData.map { it.numbersList }
                .onEach { numbersList ->
                    stopPlaying()
                    val list = generateItemsFrom(numbersList)
                    _initialItems.update { list }

                    resetStepAndOperation()
                }
                .launchIn(defaultScope)

            sortIterator.filterNotNull().onEach { iterator ->
                _currentState.update { iterator.getCurrentState() }
                _currentOperation.update { null }
            }.launchIn(defaultScope)
        }
    }

    fun getOperationSize(): Int {
        return sortIterator.value?.getOperationSize?.invoke() ?: 0
    }


    private fun resetStepAndOperation() {
        _currentStep.update { 0 }
        _currentOperation.update { null }
    }

    private suspend fun nextStep() {
        coroutineScope {
            prevJob?.join()
            nextJob = launch {
                sortIterator.value?.let { iterator ->

                    val operation = getNextSortOperationUseCase.execute(iterator)
                    _currentOperation.update { operation }
                    _currentState.update { iterator.getCurrentState() }

                    _currentStep.update { iterator.getCurrentStep() + 1 }
                }
                nextJob = null
            }
        }
    }


    fun onClickNextStep() {
        viewModelScope.launch {
            stopPlaying()
            nextStep()
        }
    }

    private suspend fun play(until: Int? = null) {
        coroutineScope {
            withContext(defaultScope.coroutineContext) {
                _isPlaying.update { true }

                playJob =
                    combine(_currentStep, delay, sortIterator) { step, delay, iterator ->
                        Triple(
                            step,
                            delay,
                            iterator
                        )
                    }
                        .onEach {
                            val (_, delay) = it
                            nextStep()
                            delay(delay)
                        }
                        .takeWhile { x ->
                            !sortIterator.value!!.isSorted() && (until?.let { x.first < it }
                                ?: true)
                        }
                        .onCompletion {
                            stopPlaying()
                        }
                        .launchIn(defaultScope)
            }

        }
    }


    fun onPlay() {
        viewModelScope.launch {
            playJob?.let {
                stopPlaying()
                return@launch
            }
            val until: Int? = null
            stopPlaying()
            play(until)
        }
    }

    private suspend fun prevStep() {
        coroutineScope {
            if (_currentStep.value <= 0) {
                return@coroutineScope
            }

            select<Unit> {
                nextJob?.onJoin
            }
            nextJob?.join()
            prevJob = launch {
                sortIterator.value?.let { iterator ->
                    val operation = getPreviousSortOperationUseCase.execute(iterator)
                    _currentOperation.update { operation }
                    _currentState.update { iterator.getCurrentState() }
                    if (_currentStep.value > 0) {
                        _currentStep.update { iterator.getCurrentStep() + 1 }
                    }
                }
                prevJob = null
            }
        }
    }

    fun onClickPrevStep() {
        viewModelScope.launch {
            stopPlaying()
            prevStep()
        }
    }

    private suspend fun setStep(step: Int) {
        coroutineScope {
            if (step == _currentStep.value) {
                return@coroutineScope
            }

            sortIterator.value?.let { iterator ->
                val operation = setSortStepUseCase.execute(iterator, step)

                _currentOperation.update { operation }
                _currentState.update { iterator.getCurrentState() }

                val opSize = getOperationSize()
                if (step in 0..opSize) {
                    _currentStep.update { step }
                }
            }
        }
    }

    private fun onSetStep(step: Int) {
        setStepJob?.cancel()
        setStepJob = viewModelScope.launch {
            stopPlaying()
            setStep(step)
            setStepJob = null
        }
    }

    fun onStepValueChange(value: Int) {
        viewModelScope.launch {
            onSetStep(value)
        }
    }

    fun onSlideChange(value: Float) {
        viewModelScope.launch {
            val step = (value * getOperationSize()).roundToInt()
            onSetStep(step)
        }
    }

    fun onAlgorithmChanged(algorithmName: SortAlgorithmName) {
        viewModelScope.launch {
            stopPlaying()
            resetList()
            algorithmUpdates.emit(algorithmName)
        }
    }

    private suspend fun reset(algorithmName: SortAlgorithmName? = null) {
        coroutineScope {
            stopPlaying()
            nextJob?.cancelAndJoin()
            nextJob = null
            prevJob?.cancelAndJoin()
            prevJob = null
            setStepJob?.cancelAndJoin()
            setStepJob = null

            resetList()
            algorithmUpdates.emit(algorithmName ?: this@SortViewModel.algorithmName.value)
        }
    }

    private suspend fun resetList() {
        coroutineScope {
            _currentStep.update { 0 }
            _currentState.update { _initialItems.value!! }
        }
    }

    fun onResetClick() {
        viewModelScope.launch {
            algorithmUpdates.emit(algorithmName.value)
            reset()
        }
    }


    private suspend fun stopPlaying() {
        coroutineScope {
            playJob?.cancel()
            playJob = null
            _isPlaying.update { false }
        }
    }
}
