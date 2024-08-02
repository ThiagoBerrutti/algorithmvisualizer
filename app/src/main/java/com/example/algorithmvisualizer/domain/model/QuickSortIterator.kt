//package com.example.algorithmvisualizer.domain.model
//
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.runBlocking
//import kotlin.coroutines.CoroutineContext
//
//
//class QuickSortSortIterator(items: MutableList<Item>) : SortIterator<QuickSortAction> {
//    private var state: QuickSortState = QuickSortState(items.toMutableList())
//    private val history =
//        OperationAndIndicesHistory<QuickSortAction, QuickSortIndices>(indices = QuickSortIndicesHistory())
//    private val snapshotManager= SnapshotManager<List<Item>>(10)
//    private var selectedIndices: MutableSet<Int> = mutableSetOf()
//
//    override val getOperationSize = history::getOperationSize
//    override var completedSortingAt: Int? = null
//
//    init {
//        state.stack.add(state.low to state.high)
//    }
//
//
//    override fun next(): SortOperation<QuickSortAction>? {
//
//        if (state.isSorted) return null
//
//        val nextOperationIndex = history.getHistoryIndex() + 1
//        val nextOperation = history.getOperation(nextOperationIndex)
//
//        nextOperation?.let { nextOp ->
//            val nextIndices = history.getIndices(nextOperationIndex)
//
//            nextIndices?.let {
//                state = state.copy(
//                    low = it.low,
//                    high = it.high,
//                    returnPoint = it.returnPoint,
//                    pivot = it.pivot,
//                    partitioning = it.partitioning
//                )
//            }
//
//            history.incrementHistoryIndex()
//
//            applyOperation(state.items, nextOp, false)
//            removeSelectedItemsStatus(selectedIndices)
//            applySelectedItemsStatus(nextOp.indices)
//
//            return nextOp
//        }
//
//        while (state.stack.isNotEmpty()) {
//            val (low, high) = state.stack.removeAt(state.stack.lastIndex)
//            state = state.copy(low = low, high = high)
//
//            if (low < high) {
//                var checkpoint = 1
//                if (state.returnPoint < checkpoint) {
//                    val pivotIndex = partition(state.items, low, high)
//                    state = state.copy(pivot = state.items[pivotIndex].value, returnPoint = 1)
//
//                    val pivotOperation = SortOperation(
//                        QuickSortAction.SelectingPivot,
//                        listOf(pivotIndex),
//                        listOf(state.items[pivotIndex])
//                    )
//                    history.addOperation(pivotOperation)
//                    snapshotManager.saveSnapshotIfNeeded(history.getHistoryIndex(), state.items)
//                    history.addIndices(history.getHistoryIndex(), state.getIndices())
//
//                    state = state.copy(returnPoint = checkpoint)
//
//                    return pivotOperation
//                }
//
//                checkpoint = 4
//                if (state.returnPoint < checkpoint) {
//                    val pivotIndex = state.pivot!!
//                    val partitionOperation = SortOperation(
//                        QuickSortAction.Partitioning,
//                        listOf(low, pivotIndex, high),
//                        listOf(state.items[low], state.items[pivotIndex], state.items[high])
//                    )
//                    history.addOperation(partitionOperation)
//                    applyOperation(state.items, partitionOperation, reverse = false)
//                    snapshotManager.saveSnapshotIfNeeded(history.getHistoryIndex(), state.items)
//                    history.addIndices(history.getHistoryIndex(), state.getIndices())
//                    state.stack.add(low to pivotIndex - 1)
//                    state.stack.add(pivotIndex + 1 to high)
//                    state = state.copy(returnPoint = 0)
//                    return partitionOperation
//                }
//                    state = state.copy(returnPoint = 0)
//            }
//        }
//
//
//        if (completedSortingAt == null) {
//            completedSortingAt = history.getHistoryIndex()
//        }
//        state = state.copy(isSorted = true)
//        return null
//    }
//
//    override fun prev(): SortOperation<QuickSortAction>? {
//        if (history.getHistoryIndex() < 0) return null
//
//        val curOperation = history.getCurrentOperation()
//
//        history.decrementHistoryIndex()
//        val prevOperation = history.getCurrentOperation()
//
//        state = state.copy(isSorted = false)
//
//        val prevIndices = history.getIndices(history.getHistoryIndex())
//        prevIndices?.let {
//            state = state.copy(
//                low = it.low,
//                high = it.high,
//                returnPoint = it.returnPoint,
//                pivot = it.pivot,
//                partitioning = it.partitioning
//            )
//        }
//
//        curOperation?.let {
//            applyOperation(state.items, it, true)
//        }
//
//        return prevOperation?.also { prevOp ->
//            removeSelectedItemsStatus(selectedIndices)
//            applySelectedItemsStatus(prevOp.indices)
//        }
//    }
//
//    override fun setStep(step: Int): SortOperation<QuickSortAction>? {
//        val targetIndex = if (step - 1 < 0) {
//            -1
//        } else if (step >= history.getOperationSize()) {
//            history.getOperationSize() - 1
//        } else {
//            step - 1
//        }
//
//        val (nearestSnapshotIndex, snapshot) = snapshotManager.getNearestSnapshot(
//            targetIndex.coerceAtLeast(0)
//        ) ?: (0 to state.items.toMutableList())
//        val resetItems = snapshot.toMutableList()
//
//        var i = nearestSnapshotIndex
//        do {
//            removeSelectedItemsStatus(selectedIndices, resetItems)
//            history.getOperation(i)?.let { op ->
//
//                applyOperation(resetItems, op, reverse = false)
//                selectedIndices.addAll(op.indices)
//
//                history.getIndices(i)?.let {
//                    state = state.copy(
//                        low = it.low,
//                        high = it.high,
//                        returnPoint = it.returnPoint,
//                        pivot = it.pivot,
//                        partitioning = it.partitioning
//                    )
//                }
//            }
//
//            i++
//        } while (i <= targetIndex)
//
//        history.getOperation(i - 1)?.let { op ->
//            applySelectedItemsStatus(op.indices, resetItems)
//        }
//        history.setHistoryIndex(targetIndex)
//
//        val isSorted = targetIndex == completedSortingAt
//        state = state.copy(items = resetItems, isSorted = isSorted)
//
//        return history.getCurrentOperation()
//    }
//
//    override fun isSorted(): Boolean = completedSortingAt == history.getHistoryIndex()
//
//
//    private fun applyOperation(
//        list: MutableList<Item>,
//        operation: SortOperation<QuickSortAction>,
//        reverse: Boolean,
//    ) {
//        when (operation.action) {
//            QuickSortAction.Swapping -> {
//                val (index1, index2) = operation.indices
//                if (reverse) {
//                    // Swap back to original positions
//                    val temp = list[index2]
//                    list[index2] = list[index1]
//                    list[index1] = temp
//                } else {
//                    // Perform the swap
//                    val temp = list[index1]
//                    list[index1] = list[index2]
//                    list[index2] = temp
//                }
//            }
//
//            QuickSortAction.Partitioning -> {
//                val (low, pivotIndex, high) = operation.indices
//                val pivot = list[pivotIndex]
//                var i = low - 1
//                for (j in low until high) {
//                    if (list[j].value <= pivot.value) {
//                        i++
//                        list.swap(i, j)
//                    }
//                }
//                list.swap(i + 1, pivotIndex)
//            }
//
//           else -> {}
//        }
//    }
//
//    private fun partition(items: MutableList<Item>, low: Int, high: Int): Int {
//        val initialCheckpoint = 2
//
//        val pivot = items[high]
//        var i = low - 1
//
//        for (j in low until high) {
//            var checkpoint = initialCheckpoint
//            if (state.returnPoint < 2) {
//                val comparingOperation = SortOperation(
//                    QuickSortAction.Comparing,
//                    listOf(i, j),
//                    listOf(items[i], items[j])
//                )
//                // tem q verificar se ja n existe o proximo, pro caso de setstep
//                history.addOperation(comparingOperation)
//                state = state.copy(returnPoint = checkpoint)
//
//                // precisa retornar o comparingOperation, atualizar o state até o momento.
//
//            }
//
//            checkpoint = 3
//            if (state.returnPoint < checkpoint) {
//                if (items[j].value <= pivot.value) {
//                    i++
//                    val swappingOperation = SortOperation(
//                        QuickSortAction.Swapping,
//                        listOf(i, j),
//                        listOf(items[i], items[j])
//                    )
//                    // tem q verificar se ja n existe o proximo, pro caso de setstep
//                    history.addOperation(swappingOperation)
//                    items.swap(i, j) // tem que ver o apply operation, se é isso q precisa
//                    // precisa retornar o comparingOperation, atualizar o state até o momento.
//
//                }
//                    state = state.copy(returnPoint = checkpoint)
//            }
//                    state = state.copy(returnPoint = initialCheckpoint)
//        }
//
//
//        items.swap(i + 1, high) // tem que ver o apply operation, se é isso q precisa
//        return i + 1
//    }
//
//    override fun getCurrentState(): List<Item> = state.items.toList()
//
//
//    private fun MutableList<Item>.swap(i: Int, j: Int) {
//        val temp = this[i]
//        this[i] = this[j]
//        this[j] = temp
//    }
//
//    private fun applySelectedItemsStatus(
//        indices: List<Int>,
//        list: MutableList<Item> = state.items,
//    ) {
//        indices.forEach { index ->
//            list.setStatus(index, ItemStatus.Selected)
//        }
//        selectedIndices.clear()
//        selectedIndices.addAll(indices)
//
//
//    }
//
//    private fun removeSelectedItemsStatus(
//        indices: MutableSet<Int>,
//        list: MutableList<Item> = state.items,
//    ) {
//        indices.forEach { list.setStatus(it, ItemStatus.Normal) }
//        indices.clear()
//    }
//}
