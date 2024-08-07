package com.example.algorithmvisualizer.domain.model.quicksort

import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.SortAlgorithm
import com.example.algorithmvisualizer.domain.model.SortAlgorithmName
import com.example.algorithmvisualizer.domain.model.SortIterator

class QuickSortAlgorithm(private val initialItems: List<Item>) : SortAlgorithm<QuickSortAction> {
    override fun sort(): SortIterator<QuickSortAction> {
        return QuickSortIterator(initialItems.toMutableList())
    }

    override val name = SortAlgorithmName.QuickSort
}