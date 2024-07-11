package com.example.algorithmvisualizer.domain.model

data class SortState(
    val items: MutableList<Item>,
    val currentIndex: Int = 0,
    val subIndex: Int = 0,
    val isSorted: Boolean = false,
    val returnPoint: Int = 0,
)