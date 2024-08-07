package com.example.algorithmvisualizer.domain.model.quicksort

import com.example.algorithmvisualizer.data.util.OPERATIONS_SNAPSHOT_INTERVAL
import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.ItemStatus
import com.example.algorithmvisualizer.domain.model.OperationAndIndicesHistory
import com.example.algorithmvisualizer.domain.model.SnapshotManager
import com.example.algorithmvisualizer.domain.model.SortIterator
import com.example.algorithmvisualizer.domain.util.setStatus
import com.example.algorithmvisualizer.domain.util.swap
import kotlinx.coroutines.coroutineScope


class QuickSortIterator(
    val items: List<Item>,
    private val history: OperationAndIndicesHistory<QuickSortOperation, QuickSortIndices> =
        OperationAndIndicesHistory(indices = QuickSortIndicesHistory()),
    private val snapshotManager: SnapshotManager<QuickSortState> = SnapshotManager(
        OPERATIONS_SNAPSHOT_INTERVAL
    ),
    private var state: QuickSortState = QuickSortState(items.toMutableList()),
) : SortIterator<QuickSortAction> {
    private var initialized = false

    override val getOperationSize: () -> Int = history::getOperationSize
    override var completedSortingAt: Int? = null

    override suspend fun next(): QuickSortOperation {
        if (isSorted()) {
            return history.getOperation(history.operation.getSize() - 1)!!
        }

        var checkpoint = 0

        // Verifica se existe historico a seguir
        val nextOperationIndex = history.getHistoryIndex() + 1
        val nextOperation = history.getOperation(nextOperationIndex)

        nextOperation?.let { nextOp ->
            val nextIndices = history.getIndices(nextOperationIndex)

            // Atribui indices anteriores ao state
            nextIndices?.let {
                copyToState(it)
            }

            // Muda indice do historico de operations para o próximo
            history.incrementHistoryIndex()

            // Aplica mudanças
            applyOperation(state.items, nextOp)

            // Desseleciona itens anteriores
            val indices = state.indicesStatus.keys
            removeSelectedItemsStatus(list = state.items, indices = indices)
            applyOperationIndicesStatus(state.items, nextOperation)

            return nextOp
        }

        // Inicializa
        if (!initialized) {
            initialized = true
            state.stack.add(state.low to state.high)
            val partitioningOp = QuickSortUtils.partitionOperationFactory(
                state.items, (state.low to state.high)
            )
            val indices = state.indicesStatus.keys
            removeSelectedItemsStatus(list = state.items, indices = indices)

            applyOperationAndSaveInHistoryAndSnapshots(partitioningOp)
            saveIndices()
            return partitioningOp
        }

        // Check for sort completion:
        // If the stack is empty and there is no active stack
        if (state.items.size <= 1 || (state.stack.isEmpty() && !state.partitioning)) {
            val completedOperation =
                QuickSortOperation(QuickSortAction.Completed, listOf(), listOf())

            val indices = state.indicesStatus.keys
            removeSelectedItemsStatus(list = state.items, indices = indices)

            applyOperationAndSaveInHistoryAndSnapshots(completedOperation)
            saveIndices()

            completedSortingAt = history.getHistoryIndex()
            return completedOperation
        }

        if (!state.partitioning) {
            // Comparing

            val (currentLow, currentHigh) = state.stack.last()
            state = state.copy(low = currentLow, high = currentHigh)

            if (state.low < state.high) {
                checkpoint = 1
                if (state.returnPoint < checkpoint) {
                    state = state.copy(returnPoint = checkpoint)
                    val pivotIndex = state.low + (state.high - state.low) / 2
                    state =
                        state.copy(
                            pivotValue = state.items[pivotIndex].value,
                            pivotIndex = pivotIndex
                        )

                    val selectingPivotOp =
                        QuickSortOperation(
                            QuickSortAction.SelectingPivot,
                            listOf(pivotIndex),
                            listOf(state.items[pivotIndex])
                        )

                    val indices = state.indicesStatus.keys
                    removeSelectedItemsStatus(list = state.items, indices = indices)

                    applyOperationAndSaveInHistoryAndSnapshots(selectingPivotOp)
                    saveIndices()
                    return selectingPivotOp
                }
                state = state.copy(
                    l = state.low, r = state.high,
                    partitioning = true
                )
            }
        }

        if (state.partitioning) {
            // Selecionando os índices para fazer swap.

            // Selecionando indice esquerdo. Para simular um laço, o state.returnpoint só é alterado
            // quando o item no indice l é maior ou igual ao pivot, ou l ultrapassa r
            if (state.l <= state.r) {
                checkpoint = 2
                if (state.returnPoint < checkpoint) {
                    val shouldSwap: Boolean = state.items[state.l].value >= state.pivotValue
                    val comparingOp = if (shouldSwap) {
                        state = state.copy(returnPoint = checkpoint) // ?????
                        QuickSortUtils.leftSelectedOperationFactory(
                            state.items,
                            state.pivotIndex,
                            state.l,
                            state.r
                        )
                    } else {
                        state = state.copy(returnPoint = checkpoint - 1, l = state.l + 1)
                        QuickSortUtils.comparingLeftOperationFactory(
                            state.items,
                            state.pivotIndex,
                            state.l - 1,
                            state.r
                        )
                    }

                    val indices = state.indicesStatus.keys
                    removeSelectedItemsStatus(list = state.items, indices = indices)

                    applyOperationAndSaveInHistoryAndSnapshots(comparingOp)
                    saveIndices()

                    return comparingOp
                }


                // Mudando o trecho abaixo pra enviar vários eventos durante a seleção do item DIREITO
                checkpoint = 3
                if (state.returnPoint < checkpoint) {
                    val shouldSwap = state.items[state.r].value <= state.pivotValue
                    val comparingOp = if (shouldSwap) {
                        state = state.copy(returnPoint = checkpoint)
                        QuickSortUtils.rightSelectedOperationFactory(
                            state.items,
                            state.pivotIndex,
                            state.l,
                            state.r
                        )
                    } else {
                        state = state.copy(returnPoint = checkpoint - 1, r = state.r - 1)
                        QuickSortUtils.comparingRightOperationFactory(
                            state.items,
                            state.pivotIndex,
                            state.l,
                            state.r + 1
                        )

                    }

                    val indices = state.indicesStatus.keys - state.pivotIndex
                    removeSelectedItemsStatus(list = state.items, indices = indices)

                    applyOperationAndSaveInHistoryAndSnapshots(comparingOp)
                    saveIndices()

                    return comparingOp
                }
            }

            // If i and j have crossed, we are done with this partition
            // Talvez manda um Comparing aqui
            if (state.l > state.r) {
                checkpoint = 4
                if (state.returnPoint < checkpoint) {
                    state = state.copy(returnPoint = checkpoint)
                    val sortedOp =
                        QuickSortUtils.partitionSortedOperationFactory(
                            state.items,
                            state.low,
                            state.high
                        )

                    val indices = state.indicesStatus.keys
                    removeSelectedItemsStatus(list = state.items, indices = indices)

                    applyOperationAndSaveInHistoryAndSnapshots(sortedOp)
                    saveIndices()


// #####################                    AQUI QUE REMOVE O STACK
                    state.stack.removeLastOrNull()
// #####################                    AQUI QUE REMOVE O STACK


                    return sortedOp
                }

                // Comparing
                if (state.low < state.r) {
                    state.stack.add(state.low to state.r)
                }

                // Comparing
                if (state.l < state.high) {
                    state.stack.add(state.l to state.high)
                }

                val partitionOp = QuickSortUtils.partitionOperationFactory(
                    state.items, Pair(state.low, state.r), Pair(state.l, state.high)
                )

                state = state.copy(partitioning = false, returnPoint = 0)


                val indices = state.indicesStatus.keys
                removeSelectedItemsStatus(list = state.items, indices = indices)

                applyOperationAndSaveInHistoryAndSnapshots(partitionOp)
                saveIndices()

                return partitionOp

            }
            // Swap elements at i and j
            val swapOperation = QuickSortUtils.swapOperationFactory(state.items, state.l, state.r)
            state = state.copy(l = state.l + 1, r = state.r - 1, returnPoint = 0)

            val indices = state.indicesStatus.keys
            removeSelectedItemsStatus(list = state.items, indices = indices)

            applyOperationAndSaveInHistoryAndSnapshots(swapOperation)
            saveIndices()

            return swapOperation
        }

        val sortedOperation =
            QuickSortUtils.partitionSortedOperationFactory(state.items, state.low, state.high)
        state = state.copy(returnPoint = 0)

//        applyOperationIndicesStatus(state.items, sortedOperation)
        val indices = state.indicesStatus.keys
        removeSelectedItemsStatus(list = state.items, indices = indices)
        applyOperationAndSaveInHistoryAndSnapshots(sortedOperation)
        saveIndices()

        return sortedOperation
    }

    override suspend fun prev(): QuickSortOperation? {
        return coroutineScope {
        if (history.getHistoryIndex() < 0) return@coroutineScope null

        val curOperation = history.getCurrentOperation()

        history.decrementHistoryIndex()
        val prevOperation = history.getCurrentOperation()

        val prevIndices = history.getIndices(history.getHistoryIndex())
        prevIndices?.let {
            copyToState(it)
        }

        curOperation?.let { curOp ->
            removeSelectedItemsStatus(
                list = state.items,
                ignoreStack = true,
                indices = state.indicesStatus.filter { it.value != ItemStatus.Normal }.keys
            )
            applyOperation(state.items, curOp)
        }

        return@coroutineScope prevOperation?.also { prevOp ->
            val idxs = state.stack.lastOrNull()?.run { first..second }
            idxs?.let { selectPartitionItens(it) }
            applyOperationIndicesStatus(state.items, prevOp)
        }
    }}


    override suspend fun setStep(step: Int): QuickSortOperation? {
        return coroutineScope {
        val targetIndex = if (step - 1 < 0) {
            -1
        } else if (step >= history.getOperationSize()) {
            history.getOperationSize() - 1
        } else {
            step - 1
        }


        val (nearestSnapshotIndex, snapshot) =
            snapshotManager
                .getNearestSnapshot(
                    targetIndex.coerceAtLeast(0)
                ) ?: Pair(0, QuickSortState(items.toMutableList()))

        val resetItems = snapshot.items.toMutableList()

        var i = nearestSnapshotIndex
        do {
            removeSelectedItemsStatus(list = resetItems, ignoreStack = true)
            history.getOperation(i)?.let { op ->
                applyOperation(resetItems, op)

                history.getIndices(i)?.let {
                    copyToState(it)
                }
                applyOperationIndicesStatus(resetItems, op)
            }

            i++
        } while (i <= targetIndex)

        history.setHistoryIndex(targetIndex)

        val isSorted = targetIndex == completedSortingAt
        state = state.copy(items = resetItems, isSorted = isSorted)


        val r = state.stack.lastOrNull()?.let { it.first..it.second }
        if (r != null) {
            selectPartitionItens(r)
        }
        return@coroutineScope history.getCurrentOperation()
    }}

    override fun isSorted(): Boolean =
        completedSortingAt?.let { it <= history.getHistoryIndex() } ?: false

    override fun getCurrentState(): List<Item> {
        return state.items.toList()
    }

    override fun getCurrentStep(): Int = history.getHistoryIndex()


    private fun saveIndices(historyIndex: Int? = null) {
        val index = historyIndex ?: history.getHistoryIndex()
        history.getIndices(index)?.let { return }

//        val indices = QuickSortIndices(
//            low = state.low,
//            high = state.high,
//            l = state.l,
//            r = state.r,
//            returnPoint = state.returnPoint,
//            pivot = state.pivotValue,
//            pivotIndex = state.pivotIndex,
//            partitioning = state.partitioning,
//            stack = state.stack.toMutableList()
//        )
        val indices = state.getIndices()
        history.addIndices(index, indices)
    }

    private fun removeSelectedItemsStatus(
        indices: Iterable<Int> = state.indicesStatus.keys,
        list: MutableList<Item> = state.items,
        lastStack: Pair<Int, Int>? = null,
        ignoreStack: Boolean = false,
        predicate: ((Int, Item) -> Boolean)? = null,
    ) {
        indices.toList().forEach {
            val should = predicate?.invoke(it, list[it]) ?: true
            if (should) {
                val ls = if (ignoreStack) (-1 to -1) else lastStack
                val status2 = calculateItemStatus(ItemStatus.Normal, it, list, ls)

                if (status2 == ItemStatus.Normal) {
                    state.indicesStatus.remove(it)
                } else {
                    state.indicesStatus[it] = status2
                }

                list.setStatus(it, status2)
            }
        }
    }

    private fun selectPartitionItens(range: IntRange, list: MutableList<Item> = state.items) {
        range.forEach { index ->
            if (list[index].status == ItemStatus.Normal) {
                state.indicesStatus[index] = ItemStatus.Partition
                list.setStatus(index, ItemStatus.Partition)
            }
        }
    }

    private fun applySelectedItemsStatus(
        indices: Iterable<Int>,
        status: ItemStatus? = null,
        list: MutableList<Item> = state.items,
        lastStack: Pair<Int, Int>? = null,
        predicate: ((Int, Item) -> Boolean)? = null,
    ) {
        val s1 = state.indicesStatus.toMap()

        s1.forEach {
            if (it.value != ItemStatus.Partition) {
                state.indicesStatus.remove(it.key)
            }
        }
        indices.forEach { index ->
            val should = predicate?.invoke(index, list[index]) ?: true
                    && (index in list.indices)

            if (should) {
                val newStatus = if (status != null) {
                    calculateItemStatus(status, index, list, lastStack)
                } else {
                    list[index].status
                }

                if (newStatus == ItemStatus.Normal) {
                    state.indicesStatus.remove(index)
                } else {
                    state.indicesStatus[index] = newStatus
                }
                list.setStatus(index, newStatus)
            }
        }


    }

    /** Apply the [operation] in the [list], mutating it if needed */
    private fun applyOperation(
        list: MutableList<Item>,
        operation: QuickSortOperation,
    ) {
        when (operation.action) {
            QuickSortAction.Comparing -> {}
            QuickSortAction.Swapping -> {
                if (operation.indices[0] == state.pivotIndex) {
                    state = state.copy(
                        pivotValue = state.items[operation.indices[0]].value,
                        pivotIndex = operation.indices[1]
                    )
                } else if (operation.indices[1] == state.pivotIndex) {
                    state = state.copy(
                        pivotValue = state.items[operation.indices[1]].value,
                        pivotIndex = operation.indices[0]
                    )
                }

                list.swap(operation.indices[0], operation.indices[1])
            }

            QuickSortAction.Completed -> {}
            QuickSortAction.FindingUnsorted -> {}
            QuickSortAction.PartitionSorted -> {}
            QuickSortAction.Partitioning -> {}
            QuickSortAction.SelectingPivot -> {}
            QuickSortAction.ComparingLeftWithPivot -> {}
            QuickSortAction.ComparingRightWithPivot -> {}
            QuickSortAction.LeftIndexSelected -> {}
            QuickSortAction.RightIndexSelected -> {}
        }
    }

    private fun calculateItemStatus(
        newStatus: ItemStatus,
        index: Int,
        list: List<Item> = state.items,
        lastStack: Pair<Int, Int>? = null,
    ): ItemStatus {
        val ls = lastStack ?: state.stack.lastOrNull()
        val isItemInsideCurrentPartition = ls?.let {
            listOf(ls.first, ls.second).sorted().let {
                index >= it[0] && index <= it[1]
            }
        } ?: false

        val result: ItemStatus = when (newStatus) {
            ItemStatus.Normal ->
                if (isItemInsideCurrentPartition) {
                    ItemStatus.Partition
                } else {
                    ItemStatus.Normal
                }

            ItemStatus.Selected -> ItemStatus.Selected
            ItemStatus.Static -> ItemStatus.Static
            ItemStatus.Partition -> ItemStatus.Partition

        }
        return result
    }

    private fun applyOperationIndicesStatus(
        list: MutableList<Item>,
        operation: QuickSortOperation,
    ) {
        when (operation.action) {
            QuickSortAction.Comparing -> {
                selectPartitionItens(operation.indices[0]..operation.indices[1], list)
                applySelectedItemsStatus(operation.indices, ItemStatus.Selected, list)
            }

            QuickSortAction.Completed -> {
                val indices = state.indicesStatus.keys
                removeSelectedItemsStatus(list = list, indices = indices)
            }

            QuickSortAction.FindingUnsorted -> {
                applySelectedItemsStatus(operation.indices, ItemStatus.Selected, list)
            }

            QuickSortAction.PartitionSorted -> {
                applySelectedItemsStatus(operation.indices, ItemStatus.Partition, list)
            }

            QuickSortAction.Partitioning -> {
                val r = operation.indices.sorted().let { it[0]..it.last() }
                applySelectedItemsStatus(r, ItemStatus.Partition, list)
            }

            QuickSortAction.SelectingPivot -> {
                selectPartitionItens(state.low..state.high, list)
                applySelectedItemsStatus(operation.indices, ItemStatus.Static, list)
            }

            QuickSortAction.Swapping -> {
                applySelectedItemsStatus(operation.indices, ItemStatus.Selected, list)
            }

            QuickSortAction.ComparingLeftWithPivot -> {
                applySelectedItemsStatus(listOf(state.pivotIndex), ItemStatus.Static, list) // Pivot
                applySelectedItemsStatus(
                    operation.indices.subList(1, 2 + 1),
                    ItemStatus.Selected, list
                )
            }

            QuickSortAction.ComparingRightWithPivot -> {
                applySelectedItemsStatus(listOf(state.pivotIndex), ItemStatus.Static, list) // Pivot
                applySelectedItemsStatus(
                    operation.indices.subList(1, 2 + 1),
                    ItemStatus.Selected, list
                )
            }

            QuickSortAction.LeftIndexSelected -> {
                applySelectedItemsStatus(listOf(state.pivotIndex), ItemStatus.Static, list) // Pivot
                applySelectedItemsStatus(
                    operation.indices.subList(1, 2 + 1),
                    ItemStatus.Selected, list
                )
            }

            QuickSortAction.RightIndexSelected -> {
                applySelectedItemsStatus(listOf(state.pivotIndex), ItemStatus.Static, list) // Pivot
                applySelectedItemsStatus(
                    operation.indices.subList(1, 2 + 1),
                    ItemStatus.Selected, list
                )
            }
        }

    }

    private fun applyOperationAndSaveInHistoryAndSnapshots(operation: QuickSortOperation) {
        applyOperationIndicesStatus(state.items, operation)
        applyOperation(state.items, operation)

        history.addOperation(operation)
        val historyIndex: Int = history.getHistoryIndex()
        snapshotManager.saveSnapshotIfNeeded(
            historyIndex,
            state.copy(items = state.items.toMutableList())
        )
    }

    private fun copyToState(v: QuickSortIndices) {
        state = state.copy(
            low = v.low,
            l = v.l,
            high = v.high,
            r = v.r,
            pivotValue = v.pivot,
            pivotIndex = v.pivotIndex,
            partitioning = v.partitioning,
            returnPoint = v.returnPoint,
            stack = v.stack.toMutableList()

        )
    }
}