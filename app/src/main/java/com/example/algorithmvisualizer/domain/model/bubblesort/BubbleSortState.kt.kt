package com.example.algorithmvisualizer.domain.model.bubblesort

import com.example.algorithmvisualizer.domain.model.Item

data class BubbleSortState(
    val items: MutableList<Item>,
    val currentIndex: Int = 0,
    val subIndex: Int = 0,
    val isSorted: Boolean = false,
    val returnPoint: Int = 0,
)