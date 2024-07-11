package com.example.algorithmvisualizer.domain.model

import com.example.algorithmvisualizer.data.algorithms.BubbleSortSortIterator
import com.example.algorithmvisualizer.data.algorithms.QuickSortIterator

interface SortAlgorithm<TAction : SortAction> {
    val name: SortAlgorithmName
    fun sort(): SortIterator<TAction>
}

enum class SortAlgorithmName() {
    BubbleSort, QuickSort
}

class BubbleSortAlgorithm(private val initialItems: List<Item>) : SortAlgorithm<BubbleSortAction> {
    override fun sort(): SortIterator<BubbleSortAction> {
        return BubbleSortSortIterator(initialItems.toMutableList())
    }

    override val name = SortAlgorithmName.BubbleSort
}

class QuickSortAlgorithm(private val initialItems: List<Item>) : SortAlgorithm<QuickSortAction> {
    override fun sort(): SortIterator<QuickSortAction> {
//        return QuickSortSortIterator(initialItems.toMutableList())
        return QuickSortIterator(initialItems.toMutableList())
    }

    override val name = SortAlgorithmName.QuickSort
}

interface SortAction {}

val SortAction.name: String
    get() =
        when (this) {
            is BubbleSortAction -> this.name
            is QuickSortAction -> this::class.simpleName ?:"--"
            else -> {
                throw IllegalArgumentException("Unknown SortAction")
            }
        }

enum class BubbleSortAction : SortAction {
    Init, Comparing, Swapping, Complete
}

sealed class QuickSortAction : SortAction {
    data object Comparing : QuickSortAction()
    data object Swapping : QuickSortAction()
    data object SelectingPivot : QuickSortAction()
    data object FindingUnsorted : QuickSortAction()
    data object Partitioning : QuickSortAction()
    data object PartitionSorted : QuickSortAction()
    data object Completed : QuickSortAction()
//     data class Partitioning(val l:List<SubListIndices>):QuickSortAction()
}
//enum class QuickSortAction2:SortAction{
//    Comparing, Swapping, SelectingPivot
//    data class Partitioning(val l:List<Int>)
//}
//data class SortOperation(
//    val action: SortAction2,
//    val indices: List<Int>,
//) {
//    companion object {
//        val Init: SortOperation
//            get() = SortOperation(SortAction2.Init, emptyList())
//
//        val Completed: SortOperation
//            get() = SortOperation(SortAction2.Complete, emptyList())
//    }
//}

const val OPERATIONS_SNAPSHOT_INTERVAL = 5


//class Algorithm(
////    private val onStep: suspend (SortAction) -> Unit = { _ -> },
//    private val items: MutableList<Item>,
////    private val scope: CoroutineScope
//)
//{
//    private val _operations =
////        mutableListOf<SortOperation>()
//        mutableListOf(SortOperation.Init)
//    //        mutableListOf<SortOperation>(SortOperation(SortAction2.Init, emptyList()))//, emptyList()))
//    val operations get() = _operations.toList()
//
//    private val _currentOperation = MutableStateFlow<SortOperation?>(null)
//    val currentOperation: StateFlow<SortOperation?>
//        get() = _currentOperation.asStateFlow()
//
//    private val _items: MutableList<Item> = items.toMutableList()
//
//    private val _currentState = MutableStateFlow(items.toMutableList())
//    val currentState: StateFlow<List<Item>> get() = _currentState
//
//    private val snapshots = mutableListOf<Pair<Int, List<Item>>>()
//
//    init {
//        snapshots.add(0 to items.toList())
//    }
//
//    // vai criar as operações
//    suspend fun sort() {
//        coroutineScope {
//            val list = items.toMutableList()
//
//            for (i in list.indices) {
//                for (j in 0 until list.size - i - 1) {
//                    addOperation(SortAction.Comparing, listOf(j, j + 1))
//                    applyOperation(list, _operations.last())
//                    tryAddSnapshot(list)
//
//                    if (list[j].value > list[j + 1].value) {
//                        addOperation(SortAction.Swapping, listOf(j, j + 1))
//                        applyOperation(list, _operations.last())
//                        tryAddSnapshot(list)
////                        if (_operations.isNotEmpty()) {
////                        }
//
//                    }
//                }
//            }
//
//            addOperation(SortAction.Complete, emptyList())
//
//            logSnapshotValues(0)
//            logSnapshotValues(1)
//            logOperations()
//        }
//    }
//
//
//    private fun addOperation(
//        action: SortAction,
//        indices: List<Int>,
//    ) {
//        val operation = SortOperation(action, indices)//, itemsInvolved)
//        _operations.add(operation)
//    }
//
//    private fun tryAddSnapshot(list: MutableList<Item>): Boolean {
//        // Capture snapshots at regular intervals (e.g., every 100 operations)
//        return if (_operations.size % OPERATIONS_SNAPSHOT_INTERVAL == 0) {
//
//            val snapshotList = list.toMutableList()
//
//            snapshots.add(Pair(_operations.size, snapshotList))
//            Log.d("ADD_TEST", "${_operations.size}; ${snapshots.map { it.first }}")
//            true
//        } else {
//            false
//        }
//    }
//
//    private fun emitCurrentOperation(operation: SortOperation) {
//        _currentOperation.value = operation
//    }
//
//    suspend fun applyOperationsUpTo(step: Int, emit: Boolean = true) {
//        coroutineScope {
//            launch {
//                val index = (step)
//
//                Log.d("STEP_TEST","$step; ${_operations.size}; ${_operations.lastIndex}")
//
//                if (index > _operations.lastIndex) {
////                    emitCurrentOperation(SortOperation.Completed)
//                    return@launch
//                }
//
//                _currentState.update { list ->
//                    val newList = list
//
//                    val snapshotIndex = snapshots.indexOfLast { it.first <= step }
//                    val start = if (snapshotIndex != -1) {
//                        val (operationIndex, snapshot) = snapshots[snapshotIndex]
//                        newList.clear()
//                        newList.addAll(snapshot)
//
//                        operationIndex
//                    } else {
//                        0
//                    }
//
////                    var i = start
////                    Log.d(
////                        "applyOperationsUpTo_TEST33",
////                        "i: $i; start: $start; index: $index; opsize: ${_operations.size} op: ${_operations[i].action.name} " +
////                                "(${_operations[i].indices.joinToString(separator = ", ")}); lastIndex: ${_operations.lastIndex}"
////                    )
//
//                    for (i in start..index) {
////                        Log.d("applyOperationsUpTo_TEST WHILE", "entou while")
//////                        if (i <= _operations.lastIndex) {
////                        Log.d("applyOperationsUpTo_TEST < lastindex", "entou if")
////                        Log.d(
////                            "applyOperationsUpTo_TEST",
////                            "i: $i; start: $start; index: $index; opsize: ${_operations.size} op: ${_operations[i].action.name} " +
////                                    "(${_operations[i].indices.joinToString(separator = ", ")}); lastIndex: ${_operations.lastIndex}"
////                        )
//
//                        getPrevOperation(i)?.let {
//                            setItemStatusAt(newList, it.indices, ItemStatus.Normal)
//                        }
//
//                        setItemStatusAt(newList, _operations[i].indices, ItemStatus.Static)
//                        if (i < index) {
//                            applyOperation(newList, _operations[i])
//                        }
////                        }
//                    }
//
//
//                    emitCurrentOperation(_operations[index])
//
//
//
//                    newList
//                }
//
//            }
//        }
//    }
//
//    private fun logSnapshotValues(index: Int = 0) {
//        val l = snapshots[index].second.joinToString(separator = ", ") { "${it.value}" }
//        Log.d("SNAPSHOT_VALUES_TEST_$index", l)
//    }
//
//    private fun logOperations(){
//        val l = _operations.joinToString(separator = ", ") { it.action.name }
//        Log.d("OPERATIONS_TEST",l)
//    }
//
//
//    private suspend fun applyOperation(
//        list: MutableList<Item>,
//        operation: SortOperation,
//    ) {
//        coroutineScope {
//            launch {
//
//                operation.indices
////
//                when (operation.action) {
//                    SortAction.Init -> {
//                        list.clear();
//                        list.addAll(items.toMutableList())
//                    }
//
//                    SortAction.Comparing -> {
//
////                        updateSelectedItems(items, operationIndex)
//                    }
//
//                    SortAction.Swapping -> {
//                        val (index1, index2) = operation.indices
//                        Log.d(
//                            "applyOperationsUpTo_TEST",
//                            "values swapped BEFORE: ${list[index1].value} e ${list[index2].value} "
//                        )
//
////                        updateSelectedItems(items, operationIndex)
//                        list.swap(index1, index2)
////                        Log.d("applyOperationsUpTo_TEST","applyOperation Swap! (${operation.indices.joinToString(", ")})")
//                        Log.d(
//                            "applyOperationsUpTo_TEST",
//                            "values swapped AFTER: ${list[index1].value} e ${list[index2].value} "
//                        )
////                    }
//
//                    }
//
//                    SortAction.Complete -> {}
//                }
//
//
//            }
//        }
//
//    }
//
//    fun SortOperation.actionAsString() = "${this.action.name} " +
//            "(${this.indices.joinToString(separator = ", ")})"
//
//
//    private fun getPrevOperation(currentOperationIndex: Int): SortOperation? {
//        return if (currentOperationIndex <= 0) {
//            null
//        } else {
//            _operations[currentOperationIndex - 1]
//        }
//    }
//
//
//    fun reset() {
//        _currentState.value = items.toMutableList()
//        _currentOperation.value = null
//    }
//
//
//}

