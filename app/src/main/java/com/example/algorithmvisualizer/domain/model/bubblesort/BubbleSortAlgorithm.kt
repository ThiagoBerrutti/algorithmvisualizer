package com.example.algorithmvisualizer.domain.model.bubblesort

import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.SortAlgorithm
import com.example.algorithmvisualizer.domain.model.SortAlgorithmName
import com.example.algorithmvisualizer.domain.model.SortIterator

class BubbleSortAlgorithm(private val initialItems: List<Item>) : SortAlgorithm<BubbleSortAction> {
    override fun sort(): SortIterator<BubbleSortAction> {
        return BubbleSortSortIterator(initialItems.toMutableList())
    }

    override val name = SortAlgorithmName.BubbleSort
}