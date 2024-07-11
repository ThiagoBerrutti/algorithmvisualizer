package com.example.algorithmvisualizer.domain.model

interface SortIterator<out TAction:SortAction> {
    fun prev(): SortOperation<SortAction>?
    fun next(): SortOperation<SortAction>?
    fun setStep(step: Int): SortOperation<SortAction>?
    fun isSorted(): Boolean
    fun getCurrentState(): List<Item>

    val getOperationSize: () -> Int
    var completedSortingAt: Int?
}


fun MutableList<Item>.setStatus(index: Int, status: ItemStatus) {
    this[index] = this[index].copy(status = status)
}