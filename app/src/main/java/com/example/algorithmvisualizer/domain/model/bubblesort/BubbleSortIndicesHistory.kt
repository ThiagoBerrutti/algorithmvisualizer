package com.example.algorithmvisualizer.domain.model.bubblesort

import com.example.algorithmvisualizer.domain.model.IndicesHistory

class BubbleSortIndicesHistory: IndicesHistory<BubbleSortIndices> {
    private val indicesList = mutableMapOf<Int, BubbleSortIndices>()
    val a = mutableListOf<String>()

    override fun addIndices(operationIndex: Int, indices: BubbleSortIndices): BubbleSortIndices? {
        return indicesList.putIfAbsent(operationIndex, indices)
    }

    override fun getIndices(operationIndex: Int): BubbleSortIndices? {
        return indicesList[operationIndex]
    }
}

data class BubbleSortIndices(
    val currentIndex: Int = 0,
    val subIndex: Int = 0,
    val returnPoint: Int = 0,
)

fun BubbleSortState.getIndices() = BubbleSortIndices(currentIndex, subIndex, returnPoint)
