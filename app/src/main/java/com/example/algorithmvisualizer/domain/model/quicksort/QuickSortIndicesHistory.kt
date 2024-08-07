package com.example.algorithmvisualizer.domain.model.quicksort

import com.example.algorithmvisualizer.domain.model.IndicesHistory

class QuickSortIndicesHistory : IndicesHistory<QuickSortIndices> {
    private val indicesList = mutableMapOf<Int, QuickSortIndices>()

    override fun addIndices(operationIndex: Int, indices: QuickSortIndices): QuickSortIndices? {
        return indicesList.putIfAbsent(operationIndex, indices)
    }

    override fun getIndices(operationIndex: Int): QuickSortIndices? {
        return indicesList[operationIndex]
    }
}

data class QuickSortIndices(
    val low: Int,
    val high: Int,
    val l: Int,
    val r: Int,
    val returnPoint: Int,

    /* TODO: os campos abaixo não devem estar nessa classe, mas ainda não decidi onde colocar
        os valores 'pivot' e 'partitioning'. sem falar que outros algoritmos podem ter outras
        variaveis especificas para seu funcionamento */
    val pivot: Int,
    val pivotIndex: Int,
    val partitioning: Boolean,
    val stack: MutableList<Pair<Int, Int>>,
)

fun QuickSortState.getIndices() = QuickSortIndices(
    low = low,
    high = high,
    l = l,
    r = r,
    returnPoint = returnPoint,
    pivot = pivotValue,
    pivotIndex = pivotIndex,
    partitioning = partitioning,
    stack = stack.toMutableList()
)