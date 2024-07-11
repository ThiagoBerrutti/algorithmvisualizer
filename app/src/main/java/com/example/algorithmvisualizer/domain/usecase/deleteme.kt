//package com.example.algorithmvisualizer.domain.usecase
//
//import com.example.algorithmvisualizer.data.algorithms.comparingOperationFactory
//import com.example.algorithmvisualizer.data.algorithms.getLeftSwapIndex
//import com.example.algorithmvisualizer.data.algorithms.getRightSwapIndex
//import com.example.algorithmvisualizer.data.algorithms.partitionOperationFactory
//import com.example.algorithmvisualizer.data.algorithms.partitionSortedOperationFactory
//import com.example.algorithmvisualizer.data.algorithms.selectPartition
//import com.example.algorithmvisualizer.data.algorithms.selectPivot
//import com.example.algorithmvisualizer.data.algorithms.swapOperationFactory
//import com.example.algorithmvisualizer.domain.model.Item
//import com.example.algorithmvisualizer.domain.model.QuickSortAction
//import com.example.algorithmvisualizer.domain.model.QuickSortIndices
//import com.example.algorithmvisualizer.domain.model.QuickSortState
//import com.example.algorithmvisualizer.domain.model.SortOperation
//import com.example.algorithmvisualizer.domain.model.swap
//
//class deleteme {
//    override fun next(): SortOperation<QuickSortAction>? {
//        var checkpoint = 0
//
//        // Verifica se existe historico a seguir
//        val nextOperationIndex = history.getHistoryIndex() + 1
//        val nextOperation = history.getOperation(nextOperationIndex)
//
//        nextOperation?.let { nextOp ->
//            val nextIndices = history.getIndices(nextOperationIndex)
//
//            // Atribui indices anteriores ao state
//            nextIndices?.let {
//                copyToState(it)
//            }
//
//            // Muda indice do historico de operations para o próximo
//            history.incrementHistoryIndex()
//
//            // Aplica mudanças
//            applyOperation(state.items, nextOp)
//
//            // Desseleciona itens anteriores
////            removeSelectedItemsStatus(selectedIndices)
//
//            // Seleciona itens atuais
////            applySelectedItemsStatus(nextOp.indices)
//
//            return nextOp
//        }
//
//        if (stack.isEmpty() && !state.partitioning) {
//            completedSortingAt = history.getHistoryIndex()
//            return null
//        }
//
//        if (!state.partitioning) {
//            // Comparing
//            checkpoint++
//            if (state.returnPoint < checkpoint) {
//                state = state.copy(returnPoint = checkpoint)
//                val comparingOp =
//                    comparingOperationFactory(state.items, stack.last().first, stack.last().second)
//                applyOperationAndSaveInHistoryAndSnapshots(comparingOp)
//                saveIndices()
//                return comparingOp
//            }
//
//            val (currentLow, currentHigh) = stack.last() // remove daqui
//            state = state.copy(low = currentLow,high = currentHigh)
//
//            if (state.low < state.high) {
//                checkpoint++
//                if (state.returnPoint < checkpoint) {
//                    state = state.copy(returnPoint = checkpoint)
//                    val pivotIndex = state.low + (state.high - state.low) / 2
//                    selectPivot(state.items, pivotIndex)
//                    state = state.copy(pivot = state.items[pivotIndex].value)
//
//                    val selectingPivotOp =
//                        SortOperation(
//                            QuickSortAction.SelectingPivot,
//                            listOf(pivotIndex),
//                            listOf(state.items[pivotIndex])
//                        )
//                    applyOperationAndSaveInHistoryAndSnapshots(selectingPivotOp)
//                    saveIndices()
//                    return selectingPivotOp
//                }
//                state = state.copy(l = state.low, r = state.high,
//                    partitioning = true)
//            }
//        }
//
//        if (state.partitioning) {
//            selectPartition(state.items, state.low, state.high)
//
//            // As partitions sao criadas a partir desses indices.
//            // Provavelmente tem que mandar as operações a partir daqui
//
//            checkpoint++
//            if (state.returnPoint < checkpoint) {
//                state = state.copy(returnPoint = checkpoint)
//                state = state.copy(l = getLeftSwapIndex(state.items, state.l, state.pivot),
//                    r = getRightSwapIndex(state.items, state.r, state.pivot)
//                )
//
//                val operation = SortOperation(
//                    QuickSortAction.FindingUnsorted,
//                    listOf(state.l, state.r), listOf(state.items[state.l], state.items[state.r])
//                )
//
//                applyOperationAndSaveInHistoryAndSnapshots(operation)
//                saveIndices()
//                return operation
//            }
//
//            // If i and j have crossed, we are done with this partition
//            // Talvez manda um Comparing aqui
//            if (state.l > state.r) {
//                checkpoint++
//                if (state.returnPoint < checkpoint) {
//                    state = state.copy(returnPoint = checkpoint)
//
//                    val removed = stack.removeLastOrNull()
//                    val comparingOperation = comparingOperationFactory(state.items, state.l, state.high)
//                    applyOperationAndSaveInHistoryAndSnapshots(comparingOperation)
//                    saveIndices()
//
//                    return comparingOperation
//                }
//
//                // Comparing
//                if (state.low < state.r) {
//                    stack.add(state.low to state.r)
//                }
//
//                // Comparing
//                if (state.l < state.high) {
//                    stack.add(state.l to state.high)
//                }
//
//                val partitionOp = partitionOperationFactory(
//                    state.items, Pair(state.low, state.r), Pair(state.l, state.high)
//                )
//
//                state = state.copy(partitioning = false, returnPoint = 0)
//
//                applyOperationAndSaveInHistoryAndSnapshots(partitionOp)
//                saveIndices()
//
//                return partitionOp
//
//            }
//            // Swap elements at i and j
//            val swapOperation = swapOperationFactory(state.items, state.r, state.l)
//            state = state.copy(l = state.l+1, r= state.r-1,returnPoint = 0)
//
//            applyOperationAndSaveInHistoryAndSnapshots(swapOperation)
//            saveIndices()
//
//            return swapOperation
//        }
//
//        val sortedOperation = partitionSortedOperationFactory(state.items, state.low, state.high)
//
//        state = state.copy(returnPoint = 0)
//
//
//        applyOperationAndSaveInHistoryAndSnapshots(sortedOperation)
//        saveIndices()
//
//        return sortedOperation
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
//        val prevIndices = history.getIndices(history.getHistoryIndex())
//        prevIndices?.let {
//            copyToState(it)
//        }
//
//        curOperation?.let {
//            applyOperation(state.items, it)
//        }
//
//        return prevOperation?.also { prevOp ->
////            removeSelectedItemsStatus(selectedIndices)
////            applySelectedItemsStatus(prevOp.indices)
//        }
//    }
//
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
//
//        val (nearestSnapshotIndex, snapshot) =
//            snapshotManager
//                .getNearestSnapshot(
//                    targetIndex.coerceAtLeast(0)
////            )?: (0 to state)
//                )?: Pair(0, QuickSortState(items.toMutableList()))
//
//        state = snapshot.copy()
//        val resetItems = snapshot.items.toMutableList()
//
//        var i = nearestSnapshotIndex
//        do {
////            removeSelectedItemsStatus(selectedIndices, resetItems)
//            history.getOperation(i)?.let { op ->
//
//                applyOperation(resetItems, op)
////                selectedIndices.addAll(op.indices)
//
//                history.getIndices(i)?.let {
//                    copyToState(it)
//                }
//            }
//
//            i++
//        } while (i <= targetIndex)
//
//        history.getOperation(i - 1)?.let { op ->
////            applySelectedItemsStatus(op.indices, resetItems)
//        }
//        history.setHistoryIndex(targetIndex)
//
//        val isSorted = targetIndex == completedSortingAt
//        state = state.copy(items = resetItems, isSorted = isSorted)
//        return history.getCurrentOperation()
//    }
//
//    override fun isSorted(): Boolean {
//        return completedSortingAt == history.getHistoryIndex()
//    }
//
//    override fun getCurrentState(): List<Item> {
//        return state.items.toList()
//    }
//
//    private fun saveIndices(historyIndex: Int = history.getHistoryIndex()) {
//        history.getIndices(historyIndex)?.let { return }
//
//        val indices = QuickSortIndices(state.low, state.high, state.l,state.r, state.returnPoint, state.pivot, state.partitioning, stack = state.stack)
//        history.addIndices(historyIndex, indices)
//    }
//
//    /** Apply the [operation] in the [list], mutating it if needed */
//    private fun applyOperation(
//        list: MutableList<Item>,
//        operation: SortOperation<QuickSortAction>,
//    ) {
//        when (operation.action) {
//            QuickSortAction.Comparing -> {
//            }
//
//            QuickSortAction.Swapping -> {
//                list.swap(operation.indices[0], operation.indices[1])
//            }
//
//            else -> {}
//
//
//        }
//    }
//
//    private fun applyOperationAndSaveInHistoryAndSnapshots(operation: SortOperation<QuickSortAction>) {
//        applyOperation(state.items, operation)
//
//        history.addOperation(operation)
//        val historyIndex: Int = history.getHistoryIndex()
//        snapshotManager.saveSnapshotIfNeeded(historyIndex, state.copy(items = state.items.toMutableList()))
//    }
//
//    private fun copyToState(v: QuickSortIndices){
//        state = state.copy(
//            low = v.low,
//            l = v.l,
//            high = v.high,
//            r = v.r,
//            pivot = v.pivot,
//            partitioning = v.partitioning,
//            returnPoint = v.returnPoint,
//            stack = v.stack.toMutableList()
//
//        )
//    }
//
//}
//
//
//}