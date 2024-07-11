package com.example.algorithmvisualizer.domain.model

class OperationHistory<TAction:SortAction> {
    private val operations = mutableMapOf<Int, SortOperation<TAction>>()
    private var historyIndex = -1

    fun addOperation(operation: SortOperation<TAction>) {
        historyIndex++
        operations.putIfAbsent(historyIndex, operation)
    }

    fun getOperation(index: Int): SortOperation<TAction>? {
        return operations[index]
    }

    fun getCurrentOperation(): SortOperation<TAction>? {
        return getOperation(historyIndex)
    }

    fun getSize(): Int {
        return operations.size
    }

    fun getHistoryIndex(): Int {
        return historyIndex
    }

    fun decrementHistoryIndex(): Int {
        if (historyIndex >= 0) historyIndex--
        return historyIndex
    }

    fun setHistoryIndex(newIndex:Int):Int {
        if (newIndex in -1..operations.size){
            historyIndex = newIndex
        }

        return historyIndex
    }

    fun incrementHistoryIndex(): Int {
        if (historyIndex < operations.size - 1) historyIndex++
        return historyIndex
    }
}