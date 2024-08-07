package com.example.algorithmvisualizer.domain.model.quicksort

import com.example.algorithmvisualizer.domain.model.ISortOperation
import com.example.algorithmvisualizer.domain.model.Item

data class QuickSortOperation(
    override val action: QuickSortAction,
    override val indices: List<Int>,
    override val items: List<Item>,
) : ISortOperation