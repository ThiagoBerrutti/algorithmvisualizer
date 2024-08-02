package com.example.algorithmvisualizer.data.algorithms

//import com.example.algorithmvisualizer.domain.model.SortOperation
import android.util.Log
import com.example.algorithmvisualizer.data.util.QuickSortOperation
import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.ItemStatus
import com.example.algorithmvisualizer.domain.model.ItemStatus.Normal
import com.example.algorithmvisualizer.domain.model.ItemStatus.Partition
import com.example.algorithmvisualizer.domain.model.ItemStatus.Selected
import com.example.algorithmvisualizer.domain.model.ItemStatus.Static
import com.example.algorithmvisualizer.domain.model.OperationAndIndicesHistory
import com.example.algorithmvisualizer.domain.model.QuickSortAction
import com.example.algorithmvisualizer.domain.model.QuickSortIndices
import com.example.algorithmvisualizer.domain.model.QuickSortIndicesHistory
import com.example.algorithmvisualizer.domain.model.QuickSortState
import com.example.algorithmvisualizer.domain.model.SnapshotManager
import com.example.algorithmvisualizer.domain.model.SortIterator
import com.example.algorithmvisualizer.domain.model.setStatus
import com.example.algorithmvisualizer.domain.model.swap

typealias SubListIndices = Pair<Int, Int>
typealias Stack = ArrayDeque<SubListIndices>

const val SnapshotInterval = 30

class QuickSortIterator(val items: List<Item>) : SortIterator<QuickSortAction> {
    private val history =
        OperationAndIndicesHistory<QuickSortOperation, QuickSortIndices>(indices = QuickSortIndicesHistory())
    private val snapshotManager = SnapshotManager<QuickSortState>(SnapshotInterval)

    private var state = QuickSortState(items.toMutableList())
    private var initialized = false

    override val getOperationSize: () -> Int = history::getOperationSize
    override var completedSortingAt: Int? = null


    override fun next(): QuickSortOperation {
        if (isSorted()){ return history.getOperation(history.operation.getSize()-1)!! }

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
//            applyOperationIndicesStatus(state.items, nextOp)
            applyOperation(state.items, nextOp)
//            applyOperationIndicesStatus(s)

            // Desseleciona itens anteriores
//            state.indicesStatus.forEach{ i,s ->
//                state.items[i] = state.items[i].copy(status = s)
//            }
//            state.indicesStatus.clear()
//            removeSelectedItemsStatus(selectedIndices)
            val indices = state.indicesStatus.keys
            removeSelectedItemsStatus(list = state.items, indices = indices)
            applyOperationIndicesStatus(state.items, nextOperation)


            // Seleciona itens atuais

//            applySelectedItemsStatus(nextOp.indices)

            return nextOp
        }

        // Inicializa
//        if (history.getOperationSize() == 0 && state.items.isNotEmpty()) {
        if (!initialized) {
            initialized = true
            state.stack.add(state.low to state.high)
            val partitioningOp = partitionOperationFactory(
                state.items, (state.low to state.high)
            )
            val indices = state.indicesStatus.keys
            removeSelectedItemsStatus(list = state.items, indices = indices)

            applyOperationAndSaveInHistoryAndSnapshots(partitioningOp)
            saveIndices()
            return partitioningOp
        }

        if (state.items.size <= 1 || (state.stack.isEmpty() && !state.partitioning)) {
            val completedOperation = QuickSortOperation(QuickSortAction.Completed, listOf(), listOf())

            val indices = state.indicesStatus.keys
            removeSelectedItemsStatus(list = state.items, indices = indices)

            applyOperationAndSaveInHistoryAndSnapshots(completedOperation)
            saveIndices()

            completedSortingAt = history.getHistoryIndex()
            return completedOperation
//            return null
        }

        if (!state.partitioning) {
            // Comparing
//            checkpoint++
//            if (state.returnPoint < checkpoint) {
//                state = state.copy(returnPoint = checkpoint)
//                val comparingOp =
//                    comparingOperationFactory(
//                        state.items,
//                        state.stack.last().first,
//                        state.stack.last().second
//                    )
//
//                applyOperationAndSaveInHistoryAndSnapshots(comparingOp)
//                saveIndices()
//                return comparingOp
//            }

            val (currentLow, currentHigh) = state.stack.last()
            state = state.copy(low = currentLow, high = currentHigh)

            if (state.low < state.high) {
                checkpoint = 1
                if (state.returnPoint < checkpoint) {
                    state = state.copy(returnPoint = checkpoint)
                    val pivotIndex = state.low + (state.high - state.low) / 2
//                    selectPivot(state.items, pivotIndex)
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
            // As partitions sao criadas a partir desses indices.
            // Provavelmente tem que mandar as operações a partir daqui

            if (state.l <= state.r) {
                // Mudando o trecho abaixo pra enviar vários eventos durante a seleção do item esquerdo
                checkpoint = 2
                if (state.returnPoint < checkpoint) {
                    val shouldSwap: Boolean = state.items[state.l].value >= state.pivotValue
                    // TODO: essa comparação não deve ser com o indice do pivo, mas apenas dos valores do pivo e do l

                    val comparingOp = if (shouldSwap) {
                        state = state.copy(returnPoint = checkpoint) // ?????
                        leftSelectedOperationFactory(
                            state.items,
                            state.pivotIndex,
                            state.l,
                            state.r
                        )
                    } else {
                        state = state.copy(returnPoint = checkpoint - 1, l = state.l + 1)
                        comparingLeftOperationFactory(
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

                    //                if (compResult){
                    //                    state.
                    //                }

                    return comparingOp
                }


                //  TODO: Criar evento de index left selecionado

                // Mudando o trecho abaixo pra enviar vários eventos durante a seleção do item DIREITO
                checkpoint = 3
                if (state.returnPoint < checkpoint) {
                    val shouldSwap = state.items[state.r].value <= state.pivotValue
                    // TODO: essa comparação não deve ser com o indice do pivo, mas apenas dos valores do pivo e do R
                    val comparingOp = if (shouldSwap) {
                        state = state.copy(returnPoint = checkpoint) // ?????
                        rightSelectedOperationFactory(
                            state.items,
                            state.pivotIndex,
                            state.l,
                            state.r
                        )
                    } else {
                        state = state.copy(returnPoint = checkpoint - 1, r = state.r - 1)
                        comparingRightOperationFactory(
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

//            checkpoint++
//            if (state.returnPoint < checkpoint) {
//                state = state.copy(returnPoint = checkpoint)
//                // Aqui podem ser feitos diversos retornos, um para cada comparação,
//                // mas dessa forma ele já procura direto os itens que tem que ser trocados
//                // talvez seja melhor implementar cada comparação, para fins didáticos
//
//
//                val curL = getLeftSwapIndex(
//                    state.items.subList(state.l, state.high + 1),
//                    state.l,
//                    state.pivotValue
//                )
//
//                val curR = getRightSwapIndex(
//                    state.items.subList(state.low, state.r + 1),
////                    state.r,
//                    state.low,
//                    state.pivotValue
//                )
//                state = state.copy(l = curL, r = curR)
//
//
////                val operation = SortOperation(
////                    QuickSortAction.FindingUnsorted,
////                    listOf(state.l, state.r), listOf(state.items[state.l], state.items[state.r])
////                )
////
////                applyOperationAndSaveInHistoryAndSnapshots(operation)
////                saveIndices()
////                return operation
//            }

            // If i and j have crossed, we are done with this partition
            // Talvez manda um Comparing aqui
            if (state.l > state.r) {
                checkpoint = 4
                if (state.returnPoint < checkpoint) {
                    state = state.copy(returnPoint = checkpoint)
                    val sortedOp =
                        partitionSortedOperationFactory(state.items, state.low, state.high)

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

                val partitionOp = partitionOperationFactory(
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
            // INVERTI OS state.l e sttate.r
            val swapOperation = swapOperationFactory(state.items, state.l, state.r)
//            val swapOperation = swapOperationFactory(state.items, state.r, state.l)
            state = state.copy(l = state.l + 1, r = state.r - 1, returnPoint = 0)

//            applyOperationIndicesStatus(state.items, swapOperation)

            val indices = state.indicesStatus.keys
            removeSelectedItemsStatus(list = state.items, indices = indices)

            applyOperationAndSaveInHistoryAndSnapshots(swapOperation)
            saveIndices()

            return swapOperation
        }

        val sortedOperation = partitionSortedOperationFactory(state.items, state.low, state.high)
        state = state.copy(returnPoint = 0)

//        applyOperationIndicesStatus(state.items, sortedOperation)
        val indices = state.indicesStatus.keys
        removeSelectedItemsStatus(list = state.items, indices = indices)
        applyOperationAndSaveInHistoryAndSnapshots(sortedOperation)
        saveIndices()

        return sortedOperation
    }

    override fun prev(): QuickSortOperation? {
        if (history.getHistoryIndex() < 0) return null

        val curOperation = history.getCurrentOperation()

        history.decrementHistoryIndex()
        val prevOperation = history.getCurrentOperation()

        val prevIndices = history.getIndices(history.getHistoryIndex())
        prevIndices?.let {
            copyToState(it)
        }

        curOperation?.let {
            removeSelectedItemsStatus(
                list = state.items,
                ignoreStack = true,
                indices = state.indicesStatus.filter { it.value != Normal }.keys
            )
            applyOperation(state.items, it)
        }

        return prevOperation?.also { prevOp ->
//            removeSelectedItemsStatus()
//            val idxs = prevOp.indices.min()..prevOp.indices.max()
            val idxs = state.stack.lastOrNull()?.run { first..second }
            idxs?.let{ selectPartitionItens(it) }
            applyOperationIndicesStatus(state.items, prevOp)//, reverse = true)
//            applySelectedItemsStatus( prevOp.indices)
        }
    }


    override fun setStep(step: Int): QuickSortOperation? {
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

//                selectedIndices.addAll(op.indices)

                history.getIndices(i)?.let {
                    copyToState(it)
                }
                applyOperationIndicesStatus(resetItems, op)
            }

            i++
        } while (i <= targetIndex)


//        val operation = history.getOperation(i)
//        operation?.let{
//            val idxs = it.indices.min()..it.indices.max()
//            selectPartitionItens(idxs)
//        }


//        history.getOperation(i - 1)?.let { op ->
////            applySelectedItemsStatus(op.indices, resetItems)
//        }
        history.setHistoryIndex(targetIndex)

        val isSorted = targetIndex == completedSortingAt
        state = state.copy(items = resetItems, isSorted = isSorted)


        val r = state.stack.lastOrNull()?.let { it.first..it.second }
        if (r != null) {
            selectPartitionItens(r)
        }
        return history.getCurrentOperation()
    }

    override fun isSorted(): Boolean =  completedSortingAt?.let{ it <= history.getHistoryIndex()} ?: false

    override fun getCurrentState(): List<Item> {
        return state.items.toList()
    }

    override fun getCurrentStep(): Int = history.getHistoryIndex()


    private fun saveIndices(historyIndex: Int? = null) {
        val index = historyIndex ?: history.getHistoryIndex()
        history.getIndices(index)?.let { return }

        val indices = QuickSortIndices(
            low = state.low,
            high = state.high,
            l = state.l,
            r = state.r,
            returnPoint = state.returnPoint,
            pivot = state.pivotValue,
            pivotIndex = state.pivotIndex,
            partitioning = state.partitioning,
            stack = state.stack.toMutableList()
        )
        history.addIndices(index, indices)
    }

    private fun removeSelectedItemsStatus(
        indices: Iterable<Int> = state.indicesStatus.keys,
        list: MutableList<Item> = state.items,
        lastStack: Pair<Int, Int>? = null,
        ignoreStack: Boolean = false,
        predicate: ((Int, Item) -> Boolean)? = null,
    ) {
//        val duration = measureTimeMillis {

        Log.d("REMOVESELECTEDITEMSSTATUS_TEST", "indices: $indices")
        indices.toList().forEach {
            val should = predicate?.invoke(it, list[it]) ?: true
            if (should) {
                val ls = if (ignoreStack) (-1 to -1) else lastStack
                val status2 = calculateItemStatus(Normal, it, list, ls)
//                val status = if (isContainedInLastStackRange(it, list[it])) {
//                    state.indicesStatus[it] = Partition
//                    Partition
//                } else {
//                    state.indicesStatus.remove(it)
//                    Normal
//                }
//
                if (status2 == Normal) {
                    state.indicesStatus.remove(it)
                } else {
                    state.indicesStatus[it] = status2

                }

                list.setStatus(it, status2)

            }
        }
    }

//    private fun removePartitionItens(range: IntRange, list: MutableList<Item> = state.items) {
//        range.forEach { index ->
//            if (list[index].status == Partition) {
//                state.indicesStatus.remove(index)
//                list.setStatus(index, Normal)
//            }
//        }
//    }

    private fun selectPartitionItens(range: IntRange, list: MutableList<Item> = state.items) {
        range.forEach { index ->
            if (list[index].status == Normal) {
                state.indicesStatus[index] = Partition
                list.setStatus(index, Partition)
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
            if (it.value != Partition) {
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

                if (newStatus == Normal) {
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
            QuickSortAction.Comparing -> {

//                applyItemStatusAt(ItemStatus.Selected, operation.indices){ i, item ->
//                    item.status != ItemStatus.Static && i in operation.indices
//                }
            }

            QuickSortAction.Swapping -> {

                if (operation.indices[0] == state.pivotIndex) {
                    state = state.copy(
                        pivotValue = state.items[operation.indices[0]].value,
                        pivotIndex = operation.indices[1] //operation.indices[it],
                    )
                } else if (operation.indices[1] == state.pivotIndex) {
                    state = state.copy(
                        pivotValue = state.items[operation.indices[1]].value,
                        pivotIndex = operation.indices[0] //operation.indices[it],
                    )
                }

//
                list.swap(operation.indices[0], operation.indices[1])
//                operation.indices.firstOrNull { it == state.pivotIndex }?.let { it ->
//                    state = state.copy(
//                        pivotValue = state.items[it].value,
//                        pivotIndex = it //operation.indices[it],
//                    )
//                }
            }


            QuickSortAction.Completed -> {}
            QuickSortAction.FindingUnsorted -> {}
            QuickSortAction.PartitionSorted -> {}
            QuickSortAction.Partitioning -> {

            }

            QuickSortAction.SelectingPivot -> {}
            QuickSortAction.ComparingLeftWithPivot -> {}
            QuickSortAction.ComparingRightWithPivot -> {}
            QuickSortAction.LeftIndexSelected -> {}
            QuickSortAction.RightIndexSelected -> {}
        }


    }

//    private val isContainedInLastStackRange: (Int, Item) -> Boolean = { i: Int, v: Item ->
//        val lastStack = state.stack.lastOrNull()
//        lastStack?.let {
//            i in (lastStack.first..lastStack.second).sorted()
//        } ?: false
//    }

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
            } //  (ls.first..ls.second).sorted()
//            index in listOf(ls.first, ls.second).sorted()//  (ls.first..ls.second).sorted()
        } ?: false

        val result: ItemStatus = when (newStatus) {
            Normal ->
                if (isItemInsideCurrentPartition) {
                    Partition
                } else {
                    Normal
                }

            Selected -> Selected
            Static -> Static
            Partition -> Partition

        }
        return result
    }

    private fun applyOperationIndicesStatus(
        list: MutableList<Item>,
        operation: QuickSortOperation,
//        indicesStatus: MutableMap<Int, ItemStatus> = state.indicesStatus,
//        reverse: Boolean = false,
    ) {
        when (operation.action) {
            QuickSortAction.Comparing -> {


//                removeSelectedItemsStatus(list = list)
                selectPartitionItens(operation.indices[0]..operation.indices[1], list)
//                applySelectedItemsStatus( operation.indices[0]..operation.indices[1], Partition, list)
                applySelectedItemsStatus(operation.indices, Selected, list)
            }

            QuickSortAction.Completed -> {

                val indices = state.indicesStatus.keys
                removeSelectedItemsStatus(list = list, indices = indices)
//                removeSelectedItemsStatus(list = list)
            }

            QuickSortAction.FindingUnsorted -> {

//                val indices = state.indicesStatus.keys
//                removeSelectedItemsStatus(list = list, indices = indices)
//                removeSelectedItemsStatus(list = list)
                applySelectedItemsStatus(operation.indices, Selected, list)
            }

            QuickSortAction.PartitionSorted -> {

//                val indices = state.indicesStatus.keys
//                removeSelectedItemsStatus(list = list, indices = indices)
//                removeSelectedItemsStatus(list = list)
//                applySelectedItemsStatus( operation.indices[0]..operation.indices[1], Partition, list)
                applySelectedItemsStatus(operation.indices, Partition, list)
            }

            QuickSortAction.Partitioning -> {
//                val indicesL = (operation.indices[0]..operation.indices[1])//.sorted()
//                val indicesR = if (operation.indices.size > 2) {
//                    (operation.indices[2]..operation.indices[3])
//                } else null

                val r = operation.indices.sorted().let { it[0]..it.last() }

//                val r = (indicesL + (indicesR ?: emptyList())).toSet()
//                val range = r.sorted().toList().let{ it[0] to it.last()}


//                val indices = state.indicesStatus.keys
//                removeSelectedItemsStatus(list = list, indices = indices)
//                removeSelectedItemsStatus(list = list)
                applySelectedItemsStatus(r, Partition, list)
//                { i, v ->
//                    v.status == Normal
//                }
            }

            QuickSortAction.SelectingPivot -> {


//                removeSelectedItemsStatus(list = list)
//                val indices = state.low..state.high
//                val indices = state.indicesStatus.keys
//                removeSelectedItemsStatus(list = list, indices = indices)

                selectPartitionItens(state.low..state.high, list)
//                val (first, last) = state.stack.last()
//                applySelectedItemsStatus( first..last, Partition, list)
//                val partIdx = (state.low..state.high).toList()
                applySelectedItemsStatus(operation.indices, Static, list)
//                applySelectedItemsStatus(operation.indices, Selected, list)
            }

            QuickSortAction.Swapping -> {

//                val indices = state.indicesStatus.keys
//                removeSelectedItemsStatus(list = list, indices = indices)
//                removeSelectedItemsStatus(list = list)
//                applySelectedItemsStatus( operation.indices[0]..operation.indices[1], Partition, list)

                applySelectedItemsStatus(operation.indices, Selected, list)
//                applySelectedItemsStatus(listOf(state.pivotIndex), Static, list) // Pivot
            }

            QuickSortAction.ComparingLeftWithPivot -> {
//                selectPartitionItens(operation.indices[0]..operation.indices[1], list)
                applySelectedItemsStatus(listOf(state.pivotIndex), Static, list) // Pivot
                applySelectedItemsStatus(operation.indices.subList(1, 2 + 1), Selected, list)
            }

            QuickSortAction.ComparingRightWithPivot -> {
                applySelectedItemsStatus(listOf(state.pivotIndex), Static, list) // Pivot
                applySelectedItemsStatus(operation.indices.subList(1, 2 + 1), Selected, list)
            }

            QuickSortAction.LeftIndexSelected -> {
                applySelectedItemsStatus(listOf(state.pivotIndex), Static, list) // Pivot
                applySelectedItemsStatus(operation.indices.subList(1, 2 + 1), Selected, list)
            }

            QuickSortAction.RightIndexSelected -> {
                applySelectedItemsStatus(listOf(state.pivotIndex), Static, list) // Pivot
                applySelectedItemsStatus(operation.indices.subList(1, 2 + 1), Selected, list)
            }
        }

    }

    private fun applyOperationAndSaveInHistoryAndSnapshots(operation: QuickSortOperation) {
//        val lastStack = state.stack.lastOrNull()
//        lastStack?.let {
//            applySelectedItemsStatus(Partition, it.first..it.second)
//        }

//        val indices = state.indicesStatus.keys
//        removeSelectedItemsStatus(list = state.items, indices = indices)

        applyOperationIndicesStatus(state.items, operation)
        applyOperation(state.items, operation)




        history.addOperation(operation)
        val historyIndex: Int = history.getHistoryIndex()
//        history.addIndices(historyIndex, state.getIndices())
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


//fun selectPivot(array: MutableList<Item>, index: Int) {
//    array.forEachIndexed { i, item -> array[i] = item.copy(status = Normal) }
//    array[index] = array[index].copy(status = Static)
//}
//
//fun selectPartition(array: MutableList<Item>, start: Int, end: Int) {
//    array.forEachIndexed { i, item ->
//        if (i in (start..end)) {
//            if (item.status != Static) {
//                array[i] = item.copy(status = Selected)
//            }
//        } else {
//            array[i] = item.copy(status = Normal)
//        }
//    }
//
//}


fun partitionOperationFactory(
    arr: List<Item>,
    indicesLeft: SubListIndices? = null,
    indicesRight: SubListIndices? = null,
): QuickSortOperation {
    val indices: List<Int> = indicesLeft?.toList().orEmpty().toMutableList() +
            indicesRight?.toList().orEmpty().toMutableList()

    return QuickSortOperation(
        QuickSortAction.Partitioning,
        indices,
        indices.mapNotNull { arr.elementAtOrNull(it) }
    )
}

fun partitionSortedOperationFactory(arr: List<Item>, leftIndex: Int, rightIndex: Int) =
    QuickSortOperation(
        QuickSortAction.PartitionSorted,
        listOf(leftIndex, rightIndex),
        listOfNotNull(
            arr.elementAtOrNull(leftIndex),
            arr.elementAtOrNull(rightIndex)
        )
    )

fun swapOperationFactory(
    arr: List<Item>,
    leftIndex: Int,
    rightIndex: Int,
): QuickSortOperation {
    return QuickSortOperation(
        QuickSortAction.Swapping,
        listOf(leftIndex, rightIndex),
        listOfNotNull(
            arr.elementAtOrNull(leftIndex)?.copy(),
            arr.elementAtOrNull(rightIndex)?.copy()
        )
    )
}


fun comparingOperationFactory(arr: List<Item>, leftIndex: Int, rightIndex: Int) =
    QuickSortOperation(
        QuickSortAction.Comparing,
        listOf(leftIndex, rightIndex),
        listOfNotNull(arr.elementAtOrNull(leftIndex), arr.elementAtOrNull(rightIndex))
    )


fun comparingLeftOperationFactory(
    arr: List<Item>,
    pivotIndex: Int,
    leftIndex: Int,
    rightIndex: Int,
) = QuickSortOperation(
    QuickSortAction.ComparingLeftWithPivot,
    listOf(pivotIndex, leftIndex, rightIndex),
    listOfNotNull(
        arr.elementAtOrNull(pivotIndex),
        arr.elementAtOrNull(leftIndex),
        arr.elementAtOrNull(rightIndex),
    )
)

fun comparingRightOperationFactory(
    arr: List<Item>,
    pivotIndex: Int,
    leftIndex: Int,
    rightIndex: Int,
) =
    QuickSortOperation(
        QuickSortAction.ComparingRightWithPivot,
        listOf(pivotIndex, leftIndex, rightIndex),
        listOfNotNull(
            arr.elementAtOrNull(pivotIndex),
            arr.elementAtOrNull(leftIndex),
            arr.elementAtOrNull(rightIndex),
        )
    )

fun leftSelectedOperationFactory(
    arr: List<Item>,
    pivotIndex: Int,
    leftIndex: Int,
    rightIndex: Int,
) =
    QuickSortOperation(
        QuickSortAction.LeftIndexSelected,
        listOf(pivotIndex, leftIndex, rightIndex),
        listOfNotNull(
            arr.elementAtOrNull(pivotIndex),
            arr.elementAtOrNull(leftIndex),
            arr.elementAtOrNull(rightIndex),
        )
    )

fun rightSelectedOperationFactory(
    arr: List<Item>,
    pivotIndex: Int,
    leftIndex: Int,
    rightIndex: Int,
) =
    QuickSortOperation(
        QuickSortAction.RightIndexSelected,
        listOf(pivotIndex, leftIndex, rightIndex),
        listOfNotNull(
            arr.elementAtOrNull(pivotIndex),
            arr.elementAtOrNull(leftIndex),
            arr.elementAtOrNull(rightIndex),
        )
    )


//
//fun selectingPivotOperationFactory(arr: List<Item>, leftIndex: Int, rightIndex: Int) =
//    SortOperation(
//        QuickSortAction.SelectingPivot,
//        listOf(leftIndex, rightIndex),
//        listOfNotNull(arr.elementAtOrNull(leftIndex), arr.elementAtOrNull(rightIndex))
//    )

//fun Stack.addSublistIndices(startIndex: Int, endIndex: Int) = this.add(startIndex to endIndex)

//fun selectPivotValue(arr: List<Item>) = arr[0].value
fun getLeftSwapIndex(arr: List<Item>, leftIndex: Int, pivotValue: Int): Int {
//    val sl = arr.subList(leftIndex, arr.size)
    val idx = arr.indexOfFirst { it.value >= pivotValue }
    val res = idx + leftIndex
    return res
}

fun getRightSwapIndex(arr: List<Item>, leftIndex: Int, pivotValue: Int): Int {

    val res = arr.indices.reversed()
        .firstOrNull { arr[it].value <= pivotValue } ?: (arr.size)
//    val res = subList.indices.reversed()
//        .firstOrNull { arr[it].value <= pivotValue } ?: -1

    return res + leftIndex
}


////////////////////


//fun quickSort(arr: IntArray) {
//    if (arr.isEmpty()) return
//
//    // Armazena os indices dos subarrays que estao sendo comparados
//    val stack = ArrayDeque<Pair<Int, Int>>()
//    // Inicia com o array inteiro
//    stack.add(0 to arr.size - 1)
//
//    // Repete até não ter mais subarrays. A primeira iteracao sempre vai ter (foi add acima)
//    while (stack.isNotEmpty()) {
//        // Captura os índices que tem que se organizar no momento e remove da stack
//        val (low, high) = stack.removeLast()
//
//        if (low < high) {
//            val pivotIndex = partition(arr, low, high)
//            // Subdivide a particao atual em duas: antes do pivot - do pivot em diante
//            // Faz isso adicionando ao stack de indices que precisam ser reordenados
//            stack.add(low to pivotIndex - 1)
//            stack.add(pivotIndex + 1 to high)
//        }
//    }
//}
//
//// Ele TRANSFORMA o sub-array (arr) reordenando os indices (low..high)
//// Retorna on índice do pivot
//fun partition(arr: IntArray, low: Int, high: Int): Int {
//    val pivotIndex = high
//    val pivot = arr[pivotIndex]
//    var i = low - 1
//    for (j in low until high) {
//        // OPERATION: Comparing
//        if (arr[j] <= pivot) {
//            i++
//            // OPERATION: Swapping
//            swap(arr, i, j)
//        }
//    }
//    swap(arr, i + 1, high)
//    return i + 1
//}
//
//fun swap(arr: IntArray, i: Int, j: Int) {
//    val temp = arr[i]
//    arr[i] = arr[j]
//    arr[j] = temp
//}

