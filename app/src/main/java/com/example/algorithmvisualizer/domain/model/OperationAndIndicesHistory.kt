package com.example.algorithmvisualizer.domain.model

import com.example.algorithmvisualizer.data.util.ISortOperation

class OperationAndIndicesHistory<TOperation: ISortOperation, TIndices>(
    val operation: OperationHistory<TOperation> = OperationHistory(),
    val indices: IndicesHistory<TIndices>,
) {
    // Operations
    fun addOperation(operation: TOperation) = this.operation.addOperation(operation)
    fun getOperation(index: Int): TOperation? = operation.getOperation(index)
    fun getCurrentOperation(): TOperation? = operation.getCurrentOperation()
    fun getOperationSize(): Int = operation.getSize()
    fun getHistoryIndex(): Int = operation.getHistoryIndex()
    fun decrementHistoryIndex(): Int = operation.decrementHistoryIndex()
    fun setHistoryIndex(newIndex: Int): Int = operation.setHistoryIndex(newIndex)
    fun incrementHistoryIndex(): Int = operation.incrementHistoryIndex()

    // Indices
    fun getIndices(operationIndex: Int): TIndices? = indices.getIndices(operationIndex)
    fun addIndices(operationIndex: Int, indices: TIndices): TIndices? =
        this.indices.addIndices(operationIndex, indices)
}






