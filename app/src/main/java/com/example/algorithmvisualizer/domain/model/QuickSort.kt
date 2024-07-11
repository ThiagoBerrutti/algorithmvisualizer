package com.example.algorithmvisualizer.domain.model

data class QuickSortState(
    val items: MutableList<Item> = mutableListOf(),
    val low: Int = 0,
    val high: Int = items.lastIndex,
    val l: Int = -1,
    val r: Int = high,
    val stack: MutableList<Pair<Int, Int>> = mutableListOf(),
    val returnPoint: Int = 0,
    val pivot: Int = 0,
    val partitioning: Boolean = false,
    val indicesStatus: MutableMap<Int,ItemStatus> = mutableMapOf(),
    val isSorted: Boolean = false,
)

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

    /* TODO: as variaveis abaixo não devem estar nessa classe, mas ainda não decidi onde colocar
        os valores 'pivot' e 'partitioning'. sem falar que outros algoritmos podem ter outras
        variaveis especificas para seu funcionamento */
    val pivot: Int,
    val partitioning: Boolean,
    val stack: MutableList<Pair<Int, Int>>,
)

fun QuickSortState.getIndices() = QuickSortIndices(
    low= low,
    high= high,
    l=l,
    r= r,
    returnPoint = returnPoint,
    pivot = pivot,
    partitioning = partitioning,
    stack= stack
)
