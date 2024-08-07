package com.example.algorithmvisualizer.domain.model

interface SortAlgorithm<TAction : SortAction> {
    val name: SortAlgorithmName
    fun sort(): SortIterator<TAction>
}

enum class SortAlgorithmName() {
    BubbleSort, QuickSort
}

