package com.example.algorithmvisualizer.presentation.sort

//import com.example.algorithmvisualizer.domain.model.SortOperation
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algorithmvisualizer.data.repository.PreferencesRepository
import com.example.algorithmvisualizer.data.util.ISortOperation
import com.example.algorithmvisualizer.domain.model.BubbleSortAlgorithm
import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.QuickSortAlgorithm
import com.example.algorithmvisualizer.domain.model.SortAlgorithmName
import com.example.algorithmvisualizer.presentation.utils.generateItemsFrom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
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
import kotlinx.coroutines.withContext
import java.util.UUID
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
//        val items: List<Item>,
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
//    val uiState = MutableStateFlow<SortScreenlUiState>(SortScreenlUiState.Loading)

    private val defaultScope = CoroutineScope(Dispatchers.Default)
    private val ioScope = CoroutineScope(Dispatchers.IO)
//    private val mainScope = CoroutineScope(Dispatchers.Main)
//    private val unconfinedScope = CoroutineScope(Dispatchers.Unconfined)

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
        .stateIn(ioScope, SharingStarted.Eagerly, null)

    private val _currentState = MutableStateFlow<List<Item>?>(null)

    @OptIn(FlowPreview::class)
//    val currentState: StateFlow<List<Item>?>
//        get() = _currentState
////        .sample(16)
////        .debounce(100)
////        .throttle(16)
//            .stateIn(defaultScope, SharingStarted.Eagerly, _currentState.value)

    fun getOperationSize(): Int {
        return sortIterator.value?.getOperationSize?.invoke() ?: 0
    }
//    val getOperationSize: () -> Int = sortIterator.value?.getOperationSize ?: { 0 }

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int>
        get() = _currentStep
            .stateIn(defaultScope, SharingStarted.Lazily, _currentStep.value)

    private val _currentOperation = MutableStateFlow<ISortOperation?>(null)

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> get() = _isPlaying

    private var playJob: Job? = null
    private var setStepJob: Job? = null

    private val userData = preferencesRepo.userData
    private val delay = userData.map { it.delay }
    private val isSortInfoVisible = userData.map { it.isSortInfoVisible }


    private val listId = userData
        .map { it.numbersList }
        .distinctUntilChanged()
        .map { "${it.hashCode()}" }

    private val itemList = userData.map { it.numbersList }
        .distinctUntilChanged()
//        .map { ItemList(emptyList(), UUID.randomUUID().toString()) }
        .map { ItemList(emptyList(), "${it.hashCode()}") }
        .combine(_currentStep) { list, _ -> list }
        .combine(sortIterator.filterNotNull()) { list, iterator ->
            list.copy(items = iterator.getCurrentState())
        }

    val settings = userData.map {
        Triple(
            it.isSortInfoVisible,
            it.showIndices,
            it.showValues,
        )
    }

    val uiState: StateFlow<SortScreenUiState> = combine(
        settings,
        sortIterator.filterNotNull(),
        currentStep,
        _currentOperation,
        itemList
    ) { settings, iterator, step, operation, itemList ->
        SortScreenUiState.Completed(
            items = itemList,
            isSortInfoVisible = settings.first,
            showIndices = settings.second,
            showValues = settings.third,
            currentStep = step,
            currentOperation = operation
        )
    }
        .conflate()
        .stateIn(defaultScope, SharingStarted.WhileSubscribed(5_000), SortScreenUiState.Loading)

    init {
        viewModelScope.launch {
//            currentStep.onEach{
//                Log.d("SORT_VIEWMODEL_TEST step","$it")
//            }.launchIn(this)
//
//            _algorithm.onEach{
//                Log.d("SORT_VIEWMODEL_TEST _algorithn","$it")
//            }.launchIn(this)
//
//            _initialItems.onEach{
//                Log.d("SORT_VIEWMODEL_TEST initialitems","$it")
//            }.launchIn(this)
//
//            currentOperation.onEach{
//                Log.d("SORT_VIEWMODEL_TEST operation","$it")
//            }.launchIn(this)
//
//            isSortInfoVisible.onEach{
//                Log.d("SORT_VIEWMODEL_TEST issortvisible","$it")
//            }.launchIn(this)
//
//            sortIterator.onEach{
//                Log.d("SORT_VIEWMODEL_TEST sortIterator","$it")
//            }.launchIn(this)


//            uiState.value = SortScreenlUiState.Loading
//            userData.onEach {
//                Log.d("SORT_VIEWMODEL_USER_DATA", "$it")
//            }.launchIn(this)

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


    private fun resetStepAndOperation() {
        _currentStep.update { 0 }
        _currentOperation.update { null }
    }


    private suspend fun nextStep() =
        coroutineScope {
//            if (sortIterator.value!!.isSorted()) {
//                return@coroutineScope
//            }

            val operation = sortIterator.value!!.next()
            _currentOperation.update { operation }
            _currentState.update { sortIterator.value!!.getCurrentState() }

            val size = sortIterator.value!!.getOperationSize()
            _currentStep.update { sortIterator.value!!.getCurrentStep()+1 }
//            if (_currentStep.value < size) {
//                _currentStep.update { it + 1 }
//            }
        }

    fun onClickNextStep() {
        viewModelScope.launch() {
            withContext(defaultScope.coroutineContext) {
                stopPlaying()
                nextStep()
            }
        }
    }

    private suspend fun play(until: Int? = null) {
        coroutineScope {
            withContext(defaultScope.coroutineContext) {
                _isPlaying.update { true }
                val startTime = System.currentTimeMillis()

                playJob =
                    combine(_currentStep, delay, sortIterator) { step, delay, iterator ->
                        Triple(
                            step,
                            delay,
                            iterator
                        )
                    }
//                    .combine(delay) { step, delay -> step to delay }
                        .onEach {
//                        Log.d("PlayJob_emit","$it")
                            val (_, delay) = it
                            nextStep()
                            delay(delay)
                        }
                        .takeWhile { x ->
                            !sortIterator.value!!.isSorted() && (until?.let { x.first < it }
                                ?: true)
                        }
                        .onCompletion {
//                            val duration = System.currentTimeMillis() - startTime
//                        Log.d("PlayJob", "Duration: ${duration}ms")
                            stopPlaying()
                        }
                        .launchIn(defaultScope)
            }

        }
    }


    fun onPlay() {
        viewModelScope.launch() {
            playJob?.let {
                stopPlaying()
                return@launch
            }
            val UNTIL: Int? = null
            stopPlaying()
            play(UNTIL)
        }
    }

    private suspend fun prevStep() {
        withContext(defaultScope.coroutineContext) {
            if (_currentStep.value <= 0) {
                return@withContext
            }
//            stopPlaying()

            val operation = sortIterator.value!!.prev()
            _currentOperation.update { operation }
            _currentState.update { sortIterator.value!!.getCurrentState() }
            if (_currentStep.value > 0) {
                _currentStep.update { sortIterator.value!!.getCurrentStep()+1 }
//                _currentStep.update { it - 1 }
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
            _currentOperation.update { operation }
            _currentState.update { sortIterator.value!!.getCurrentState() }
            val opSize = getOperationSize()
            if (step in 0..opSize) {
                _currentStep.update { step }
            }
        }
//    }
    }

    fun onSetStep(step: Int) {
        setStepJob?.cancel()
        setStepJob = viewModelScope.launch() {
            stopPlaying()
//            setStep(42)
            setStep(step)
        }
    }

    fun onStepValueChange(value:Int){
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
            resetList()
            algorithmUpdates.emit(algorithmName ?: this@SortViewModel.algorithmName.value)
        }
    }

    private suspend fun resetList() {
//        viewModelScope.launch {
        coroutineScope {
            _currentStep.update { 0 }
            _currentState.update { _initialItems.value!! }
//                _initialItems.filterNotNull().take(1).collect{
//                    _currentState.value = it
//                }
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


    private suspend fun stopPlaying() {
        coroutineScope {
            playJob?.cancel()
            playJob = null
            _isPlaying.update { false }
        }
    }
}

//data class SortScreenUiState(
//    val items:List<Item>,
//    val isSortInfoVisible:Boolean,
//    val currentStep:Int,
//)


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
