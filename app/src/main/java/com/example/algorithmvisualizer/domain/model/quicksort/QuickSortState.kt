package com.example.algorithmvisualizer.domain.model.quicksort

import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.ItemStatus

data class QuickSortState(
    val items: MutableList<Item> = mutableListOf(),
    val low: Int = 0,
    val high: Int = items.lastIndex,
    val l: Int = -1,
    val r: Int = high,
    val stack: MutableList<Pair<Int, Int>> = mutableListOf(),
    val returnPoint: Int = 0,
    val pivotValue: Int = 0,
    val pivotIndex:Int = 0,
    val partitioning: Boolean = false,
    val indicesStatus: MutableMap<Int, ItemStatus> = mutableMapOf(),
    val isSorted: Boolean = false,
)