package com.example.algorithmvisualizer.domain.model

import com.example.algorithmvisualizer.data.util.ISortOperation

interface SortIterator<out TAction:SortAction> {
    fun prev(): ISortOperation?
    fun next(): ISortOperation?
    fun setStep(step: Int): ISortOperation?
    fun isSorted(): Boolean
    fun getCurrentState(): List<Item>
    fun getCurrentStep():Int

    val getOperationSize: () -> Int
    var completedSortingAt: Int?
}


fun MutableList<Item>.setStatus(index: Int, status: ItemStatus) {
    this[index] = this[index].copy(status = status)
}