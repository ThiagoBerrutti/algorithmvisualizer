package com.example.algorithmvisualizer.presentation.sort

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algorithmvisualizer.data.repository.PreferencesRepository
import com.example.algorithmvisualizer.domain.model.BubbleSortAlgorithm
import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.QuickSortAlgorithm
import com.example.algorithmvisualizer.domain.model.SortAction
import com.example.algorithmvisualizer.domain.model.SortAlgorithmName
import com.example.algorithmvisualizer.domain.model.SortOperation
import com.example.algorithmvisualizer.presentation.utils.generateStaticItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class SortViewModel @Inject constructor(
    private val preferencesRepo: PreferencesRepository,
) :
    ViewModel() {

    private val _initialItems: MutableStateFlow<List<Item>> = MutableStateFlow(emptyList())

    val algorithmName = MutableStateFlow(SortAlgorithmName.BubbleSort)
    private val algorithmUpdates = MutableSharedFlow<SortAlgorithmName>()
    private val _algorithm = algorithmUpdates
        .onStart { emit(algorithmName.value) }
        .onEach { algorithmName.value = it }
        .combine(_initialItems) { a, i -> Pair(a, i) }
        .map { pair ->
            val (algName, items) = pair
            when (algName) {
                SortAlgorithmName.BubbleSort -> BubbleSortAlgorithm(items)
                SortAlgorithmName.QuickSort -> QuickSortAlgorithm(items)
            }
        }

    private val sortIterator = _algorithm
        .map { algorithm -> algorithm.sort() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _currentState = MutableStateFlow<List<Item>>(emptyList())
    val currentState: StateFlow<List<Item>> get() = _currentState

    var getOperationSize: () -> Int = sortIterator.value?.getOperationSize ?: { 0 }

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> get() = _currentStep

    private val _currentOperation = MutableStateFlow<SortOperation<SortAction>?>(null)
    val currentOperation: StateFlow<SortOperation<SortAction>?> get() = _currentOperation

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private var playJob: Job? = null
    private var setStepJob: Job? = null

    private val defaultScope = CoroutineScope(Dispatchers.Default)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val userData = preferencesRepo.userData
    val delay = userData.map { it.delay }


    init {
        viewModelScope.launch {
            algorithmUpdates.emit(SortAlgorithmName.QuickSort)

            // Atualiza os itens quando houver uma mudança nos itens salvos
            userData.map { it.numbersList }
                .distinctUntilChanged()
                .onEach { numbersList ->
                    stopPlaying()
//                val list = generateItemsFrom(numbersList)
                    val list = generateStaticItems(4)
                    _initialItems.value = list

                    resetStepAndOperation()
                }
                .launchIn(ioScope)

            // Coleta os valores do Flow original e atualiza o MutableStateFlow
            sortIterator.onEach { iterator ->
                _currentState.value = iterator!!.getCurrentState()
                _currentOperation.value = null
                getOperationSize = iterator.getOperationSize
            }.launchIn(defaultScope)
        }
    }

    private fun resetStepAndOperation() {
        _currentStep.value = 0
        _currentOperation.value = null
    }


    private suspend fun nextStep() =
        coroutineScope {
            val operation = sortIterator.value!!.next()
            _currentOperation.value = operation
            _currentState.value = sortIterator.value!!.getCurrentState()

            val size = sortIterator.value!!.getOperationSize()
            if (_currentStep.value < size) {
                _currentStep.value++
            }
        }

    fun onClickNextStep() {
        viewModelScope.launch(defaultScope.coroutineContext) {
            stopPlaying()
            nextStep()
        }
    }

    private suspend fun play(until: Int? = null) {
        coroutineScope {
            _isPlaying.value = true
            val startTime = System.currentTimeMillis()

            playJob = currentStep
                .combine(delay) { step, delay -> step to delay }
                .onEach {
                    val (step, delay) = it
                    nextStep()
                    delay(delay ?: 0)
                }
                .takeWhile { x ->
                    !sortIterator.value!!.isSorted() && (until?.let { x.first < it - 1 } ?: true)
                }
                .onCompletion {
                    val duration = System.currentTimeMillis() - startTime
                    Log.d("PlayJob", "Duration: ${duration}ms")
                    stopPlaying()
                }
                .launchIn(defaultScope)

        }
    }


    fun onPlay() {
        viewModelScope.launch() {
            playJob?.let {
                stopPlaying()
                return@launch
            }
            stopPlaying()
            play()
        }
    }

    private suspend fun prevStep() {
        withContext(defaultScope.coroutineContext) {
            if (_currentStep.value <= 0) {
                return@withContext
            }
            stopPlaying()

            val operation = sortIterator.value!!.prev()
            _currentOperation.value = operation
            _currentState.value = sortIterator.value!!.getCurrentState()
            if (_currentStep.value >= 0) {
                _currentStep.value--
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
//        withContext(viewModelScope.coroutineContext + defaultScope.coroutineContext) {
        coroutineScope {
//        defaultScope.launch {
            if (step == _currentStep.value) {

//                return@withContext
                return@coroutineScope
            }

            val operation = sortIterator.value!!.setStep(step)
            _currentOperation.value = operation
            _currentState.value = sortIterator.value!!.getCurrentState()

            if (step in 0..getOperationSize()) {
                _currentStep.value = step
            }
        }
//    }
    }

    fun onSetStep(step: Int) {
        setStepJob?.cancel()
        setStepJob = viewModelScope.launch() {
            stopPlaying()
            setStep(step)
        }
    }

    fun onSlideChange(value: Float) {
        viewModelScope.launch {
            val step = (value * getOperationSize()).roundToInt()
            onSetStep(step)
        }
    }

    fun setDelay(value: Long) {
        viewModelScope.launch {
            preferencesRepo.saveDelay(value)
        }
    }

    fun hasFinishedSorting(): Boolean {
        return sortIterator.value!!.completedSortingAt != null
    }

    fun isSorted() = sortIterator.value!!.isSorted()

    fun onAlgorithmChanged(algorithmName: SortAlgorithmName) {
        viewModelScope.launch {
            stopPlaying()
            resetList()
            algorithmUpdates.emit(algorithmName)
        }
    }

    private suspend fun reset(algorithmName: SortAlgorithmName?=null){
        coroutineScope {

        stopPlaying()
        resetList()
        algorithmUpdates.emit(algorithmName ?: this@SortViewModel.algorithmName.value)
        }
    }

    private suspend fun resetList() {
//        viewModelScope.launch {
        coroutineScope {
            _currentStep.value = 0
            _currentState.value = _initialItems.value
        }
//            _currentOperation.value = null
//        }
    }

    fun onResetClick() {
        viewModelScope.launch {
            algorithmUpdates.emit(algorithmName.value)
                reset()
        }
    }

    fun onRandomClick() {
        viewModelScope.launch {
            withContext(ioScope.coroutineContext) {
                preferencesRepo.saveNumbersList(
                    (0..5).map { (2..50).random() }
                )
            }
        }
    }


    private suspend fun stopPlaying() {
        coroutineScope {
            playJob?.cancel()
            playJob = null
            _isPlaying.value = false
        }
    }
}


//class SortViewModelFactory(
//    private val initialItems: List<Item>,
//    private val repository: PreferencesRepository,
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(SortViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return SortViewModel(initialItems, repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}
