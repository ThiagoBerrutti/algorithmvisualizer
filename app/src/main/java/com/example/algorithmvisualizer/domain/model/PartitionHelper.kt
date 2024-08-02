package com.example.algorithmvisualizer.domain.model
//
//class PartitionHelper(
//    private val history: OperationAndIndicesHistory<QuickSortAction, QuickSortIndices>,
//    private val pendingOperations: MutableList<SortOperation<QuickSortAction>>,
//) {
//
//    private var i: Int = 0
//    private var j: Int = 0
//    private var pivot: Item? = null
//
//    fun partitionStep(items: MutableList<Item>, low: Int, high: Int): Int? {
//        if (pivot == null) {
//            pivot = items[high]
//            i = low - 1
//            j = low
//        }
//
//        while (j < high) {
//            val currentPivot = pivot ?: return null
//            addOperation(QuickSortAction.Comparing, listOf(j, high), listOf(items[j], currentPivot))
//
//            if (items[j].value <= currentPivot.value) {
//                i++
//                addOperation(QuickSortAction.Swapping, listOf(i, j), listOf(items[i], items[j]))
//                items.swap(i, j)
//            }
//            j++
//            return null  // Pausa a execução para emitir a operação
//        }
//
//        items.swap(i + 1, high)
//        addOperation(
//            QuickSortAction.Swapping,
//            listOf(i + 1, high),
//            listOf(items[i + 1], items[high])
//        )
//        pivot = null
//        return i + 1
//    }
//
//    private fun addOperation(action: QuickSortAction, indices: List<Int>, items: List<Item>) {
//        val operation = SortOperation(action, indices, items)
//        history.addOperation(operation)
//        pendingOperations.add(operation)
//    }
//}

fun <T> MutableList<T>.swap(index1: Int, index2: Int): MutableList<T> {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
    return this
}

