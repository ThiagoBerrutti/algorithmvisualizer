package com.example.algorithmvisualizer.domain.model

class OperationAndIndicesHistory<TAction: SortAction, TIndices>(
    val operation: OperationHistory<TAction> = OperationHistory(),
    val indices: IndicesHistory<TIndices>,
) {
    // Operations
    fun addOperation(operation: SortOperation<TAction>) = this.operation.addOperation(operation)
    fun getOperation(index: Int): SortOperation<TAction>? = operation.getOperation(index)
    fun getCurrentOperation(): SortOperation<TAction>? = operation.getCurrentOperation()
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






