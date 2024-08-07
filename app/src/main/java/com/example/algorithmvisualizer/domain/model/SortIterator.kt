package com.example.algorithmvisualizer.domain.model

interface SortIterator<out TAction : SortAction> {
    suspend fun prev(): ISortOperation?
    suspend fun next(): ISortOperation?
    suspend fun setStep(step: Int): ISortOperation?
    fun isSorted(): Boolean
    fun getCurrentState(): List<Item>
    fun getCurrentStep(): Int

    val getOperationSize: () -> Int
    var completedSortingAt: Int?
}


