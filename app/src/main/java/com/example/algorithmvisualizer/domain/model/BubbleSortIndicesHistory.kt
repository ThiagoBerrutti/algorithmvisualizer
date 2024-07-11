package com.example.algorithmvisualizer.domain.model

interface IndicesHistory<TIndices> {

    fun addIndices(operationIndex: Int, indices: TIndices): TIndices?

    fun getIndices(operationIndex: Int): TIndices?
}

class BubbleSortIndicesHistory: IndicesHistory<SortIndices>{
    private val indicesList = mutableMapOf<Int,SortIndices>()

    override fun addIndices(operationIndex: Int, indices: SortIndices): SortIndices? {
        return indicesList.putIfAbsent(operationIndex, indices)
    }

    override fun getIndices(operationIndex: Int): SortIndices? {
        return indicesList[operationIndex]
    }
}

data class SortIndices(
    val currentIndex: Int = 0,
    val subIndex: Int = 0,
    val returnPoint: Int = 0,
)

fun SortState.getIndices() = SortIndices(currentIndex, subIndex, returnPoint)
