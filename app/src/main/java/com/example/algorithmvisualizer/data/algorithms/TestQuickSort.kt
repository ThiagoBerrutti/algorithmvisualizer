package com.example.algorithmvisualizer.data.algorithms

import android.util.Log
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
import com.example.algorithmvisualizer.domain.model.SortOperation
import com.example.algorithmvisualizer.domain.model.setStatus
import com.example.algorithmvisualizer.domain.model.swap
import kotlin.system.measureTimeMillis

typealias SubListIndices = Pair<Int, Int>
typealias Stack = ArrayDeque<SubListIndices>

const val SnapshotInterval = 30

class QuickSortIterator(val items: List<Item>) : SortIterator<QuickSortAction> {
    private val history =
        OperationAndIndicesHistory<QuickSortAction, QuickSortIndices>(indices = QuickSortIndicesHistory())
    private val snapshotManager = SnapshotManager<QuickSortState>(SnapshotInterval)

    private var state = QuickSortState(items.toMutableList())
    private var initialized = false

    override val getOperationSize: () -> Int = history::getOperationSize
    override var completedSortingAt: Int? = null


    override fun next(): SortOperation<QuickSortAction>? {
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

        if (state.stack.isEmpty() && !state.partitioning) {
            completedSortingAt = history.getHistoryIndex()
            return SortOperation(QuickSortAction.Completed, listOf(), listOf())
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
                checkpoint++
                if (state.returnPoint < checkpoint) {
                    state = state.copy(returnPoint = checkpoint)
                    val pivotIndex = state.low + (state.high - state.low) / 2
//                    selectPivot(state.items, pivotIndex)
                    state = state.copy(pivot = state.items[pivotIndex].value)

                    val selectingPivotOp =
                        SortOperation(
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


//            selectPartition(state.items, state.low, state.high)

            // As partitions sao criadas a partir desses indices.
            // Provavelmente tem que mandar as operações a partir daqui

            checkpoint++
            if (state.returnPoint < checkpoint) {
                state = state.copy(returnPoint = checkpoint)
                // Aqui podem ser feitos diversos retornos, um para cada comparação,
                // mas dessa forma ele já procura direto os itens que tem que ser trocados
                // talvez seja melhor implementar cada comparação, para fins didáticos
                val curL = getLeftSwapIndex(state.items.subList(state.l, state.high+1), state.l, state.pivot)
//                val curR = getRightSwapIndex(state.items, state.r, state.pivot)
                val curR = getRightSwapIndex(state.items.subList(state.low, state.r+1), state.r, state.low, state.pivot)
                state = state.copy(l = curL, r = curR)


//                val operation = SortOperation(
//                    QuickSortAction.FindingUnsorted,
//                    listOf(state.l, state.r), listOf(state.items[state.l], state.items[state.r])
//                )
//
//                applyOperationAndSaveInHistoryAndSnapshots(operation)
//                saveIndices()
//                return operation
            }

            // If i and j have crossed, we are done with this partition
            // Talvez manda um Comparing aqui
            if (state.l > state.r) {
                checkpoint++
                if (state.returnPoint < checkpoint) {
                    state = state.copy(returnPoint = checkpoint)
                    val sortedOp =
                        partitionSortedOperationFactory(state.items, state.low, state.high)

                    val indices = state.indicesStatus.keys
                    removeSelectedItemsStatus(list = state.items, indices = indices)

                    applyOperationAndSaveInHistoryAndSnapshots(sortedOp)
                    saveIndices()


// #####################                    AQUI QUE REMOVE O STACK
                    val removed = state.stack.removeLastOrNull()
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
            val swapOperation = swapOperationFactory(state.items, state.r, state.l)
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

    override fun prev(): SortOperation<QuickSortAction>? {
        if (history.getHistoryIndex() < 0) return null

        val curOperation = history.getCurrentOperation()

        history.decrementHistoryIndex()
        val prevOperation = history.getCurrentOperation()

        val prevIndices = history.getIndices(history.getHistoryIndex())
        prevIndices?.let {
            copyToState(it)
        }

        curOperation?.let {
            removeSelectedItemsStatus(list=state.items, ignoreStack = true, indices=state.indicesStatus.filter{it.value != Normal}.keys)
            applyOperation(state.items, it)
        }

        return prevOperation?.also { prevOp ->
//            removeSelectedItemsStatus()
            val idxs = prevOp.indices.min()..prevOp.indices.max()
            selectPartitionItens(idxs)
            applyOperationIndicesStatus(state.items, prevOp, reverse=true)
//            applySelectedItemsStatus( prevOp.indices)
        }
    }


    override fun setStep(step: Int): SortOperation<QuickSortAction>? {
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

        val s =  state.indicesStatus.toMutableMap()
        var i = nearestSnapshotIndex
        do {
            removeSelectedItemsStatus(list = resetItems, ignoreStack = true )
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


        val operation = history.getOperation(i)
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



        val r = state.stack.lastOrNull()?.let {it.first..it.second}
        if (r != null) {
            selectPartitionItens(r)
        }
        return history.getCurrentOperation()
    }

    override fun isSorted(): Boolean{
        return completedSortingAt == history.getHistoryIndex()
    }

    override fun getCurrentState(): List<Item> {
        return state.items.toList()
    }

    private fun saveIndices(historyIndex: Int? = null) {
        val index = historyIndex ?: history.getHistoryIndex()
        history.getIndices(index)?.let { return }

        val indices = QuickSortIndices(
            state.low,
            state.high,
            state.l,
            state.r,
            state.returnPoint,
            state.pivot,
            state.partitioning,
            stack = state.stack.toMutableList()
        )
        history.addIndices(index, indices)
    }

    private fun removeSelectedItemsStatus(
        indices: Iterable<Int> = state.indicesStatus.keys,
        list: MutableList<Item> = state.items,
        lastStack: Pair<Int, Int>? = null,
        ignoreStack:Boolean = false,
        predicate: ((Int, Item) -> Boolean)? = null,
    ) {
//        val duration = measureTimeMillis {

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

    private fun removePartitionItens(range: IntRange, list: MutableList<Item> = state.items) {
        range.forEach { index ->
            if (list[index].status == Partition) {
                state.indicesStatus.remove(index)
                list.setStatus(index, Normal)
            }
        }
    }

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

        val s1= state.indicesStatus.toMap()

        s1.forEach {
            if (it.value != Partition) {
                state.indicesStatus.remove(it.key)
            }
        }
            indices.forEach { index ->
                val should = predicate?.invoke(index, list[index]) ?: true
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
        operation: SortOperation<QuickSortAction>,
    ) {
        when (operation.action) {
            QuickSortAction.Comparing -> {

//                applyItemStatusAt(ItemStatus.Selected, operation.indices){ i, item ->
//                    item.status != ItemStatus.Static && i in operation.indices
//                }
            }

            QuickSortAction.Swapping -> {
                list.swap(operation.indices[0], operation.indices[1])
            }


            QuickSortAction.Completed -> {}
            QuickSortAction.FindingUnsorted -> {}
            QuickSortAction.PartitionSorted -> {}
            QuickSortAction.Partitioning -> {

            }

            QuickSortAction.SelectingPivot -> {}

            else -> {}
        }


    }

    private val isContainedInLastStackRange: (Int, Item) -> Boolean = { i: Int, v: Item ->
        val lastStack = state.stack.lastOrNull()
        lastStack?.let {
            i in (lastStack.first..lastStack.second).sorted()
        } ?: false
    }

    private fun calculateItemStatus(
        newStatus: ItemStatus,
        index: Int,
        list: List<Item> = state.items,
        lastStack: Pair<Int, Int>? = null,
    ): ItemStatus {
        val ls = lastStack ?: state.stack.lastOrNull()
        val item = list[index]
//        val isItemInsideCurrentPartition = false
        val isItemInsideCurrentPartition = ls?.let {
            listOf(ls.first, ls.second).sorted().let{
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
        operation: SortOperation<QuickSortAction>,
        indicesStatus:MutableMap<Int,ItemStatus> = state.indicesStatus,
        reverse:Boolean = false
    ) {
        when (operation.action) {
            QuickSortAction.Comparing -> {


//                removeSelectedItemsStatus(list = list)
                selectPartitionItens( operation.indices[0]..operation.indices[1],list)
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
                applySelectedItemsStatus(operation.indices, Selected, list)
            }

            QuickSortAction.Partitioning -> {
                val indicesL = (operation.indices[0]..operation.indices[1])//.sorted()
                val indicesR = if (operation.indices.size > 2) {
                    (operation.indices[2]..operation.indices[3])
                } else null

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

                selectPartitionItens( state.low..state.high,list)
//                val (first, last) = state.stack.last()
//                applySelectedItemsStatus( first..last, Partition, list)
//                val partIdx = (state.low..state.high).toList()
                applySelectedItemsStatus(operation.indices, Selected, list)
            }

            QuickSortAction.Swapping -> {

//                val indices = state.indicesStatus.keys
//                removeSelectedItemsStatus(list = list, indices = indices)
//                removeSelectedItemsStatus(list = list)
//                applySelectedItemsStatus( operation.indices[0]..operation.indices[1], Partition, list)

                applySelectedItemsStatus(operation.indices, Selected, list)
            }

            else -> {}
        }

    }

    private fun applyOperationAndSaveInHistoryAndSnapshots(operation: SortOperation<QuickSortAction>) {
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
            pivot = v.pivot,
            partitioning = v.partitioning,
            returnPoint = v.returnPoint,
            stack = v.stack.toMutableList()

        )
    }


}


fun selectPivot(array: MutableList<Item>, index: Int) {
    array.forEachIndexed { i, item -> array[i] = item.copy(status = Normal) }
    array[index] = array[index].copy(status = Static)
}

fun selectPartition(array: MutableList<Item>, start: Int, end: Int) {
    array.forEachIndexed { i, item ->
        if (i in (start..end)) {
            if (item.status != Static) {
                array[i] = item.copy(status = Selected)
            }
        } else {
            array[i] = item.copy(status = Normal)
        }
    }

}


fun partitionOperationFactory(
    arr: List<Item>,
    indicesLeft: SubListIndices? = null,
    indicesRight: SubListIndices? = null,
): SortOperation<QuickSortAction.Partitioning> {
    val indices: List<Int> = indicesLeft?.toList().orEmpty().toMutableList() +
            indicesRight?.toList().orEmpty().toMutableList()

    return SortOperation(
        QuickSortAction.Partitioning,
        indices,
        indices.mapNotNull { arr.elementAtOrNull(it) }
    )
}

fun partitionSortedOperationFactory(arr: List<Item>, leftIndex: Int, rightIndex: Int) =
    SortOperation(
        QuickSortAction.PartitionSorted,
        listOf(leftIndex, rightIndex),
        listOfNotNull(
            arr.elementAtOrNull(leftIndex),
            arr.elementAtOrNull(rightIndex)
        )
    )

fun swapOperationFactory(arr: List<Item>, leftIndex: Int, rightIndex: Int): SortOperation<QuickSortAction.Swapping> {
    return SortOperation(
        QuickSortAction.Swapping,
        listOf(leftIndex, rightIndex),
        listOfNotNull(
            arr.elementAtOrNull(leftIndex)?.copy(),
            arr.elementAtOrNull(rightIndex)?.copy()
        )
)
}


fun comparingOperationFactory(arr: List<Item>, leftIndex: Int, rightIndex: Int) = SortOperation(
    QuickSortAction.Comparing,
    listOf(leftIndex, rightIndex),
    listOfNotNull(arr.elementAtOrNull(leftIndex), arr.elementAtOrNull(rightIndex))
)

fun selectingPivotOperationFactory(arr: List<Item>, leftIndex: Int, rightIndex: Int) =
    SortOperation(
        QuickSortAction.SelectingPivot,
        listOf(leftIndex, rightIndex),
        listOfNotNull(arr.elementAtOrNull(leftIndex), arr.elementAtOrNull(rightIndex))
    )

fun Stack.addSublistIndices(startIndex: Int, endIndex: Int) = this.add(startIndex to endIndex)

fun selectPivotValue(arr: List<Item>) = arr[0].value
fun getLeftSwapIndex(arr: List<Item>, leftIndex: Int, pivotValue: Int):Int {
//    val sl = arr.subList(leftIndex, arr.size)
      val idx = arr.indexOfFirst { it.value >= pivotValue }
    val res = idx + leftIndex
    return res
}
fun getRightSwapIndex(arr: List<Item>, rightIndex: Int, leftIndex:Int, pivotValue: Int): Int {

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

