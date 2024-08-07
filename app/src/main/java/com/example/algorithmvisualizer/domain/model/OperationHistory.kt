package com.example.algorithmvisualizer.domain.model

class OperationHistory<TOperation : ISortOperation> {
    private val operations = mutableMapOf<Int, TOperation>()
    private var historyIndex = -1

    fun addOperation(operation: TOperation) {
        historyIndex++
        operations.putIfAbsent(historyIndex, operation)
    }

    fun getOperation(index: Int): TOperation? {
        return operations[index]
    }

    fun getCurrentOperation(): TOperation? {
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

    fun setHistoryIndex(newIndex: Int): Int {
        if (newIndex in -1..operations.size) {
            historyIndex = newIndex
        }

        return historyIndex
    }

    fun incrementHistoryIndex(): Int {
        if (historyIndex < operations.size - 1) historyIndex++
        return historyIndex
    }
}