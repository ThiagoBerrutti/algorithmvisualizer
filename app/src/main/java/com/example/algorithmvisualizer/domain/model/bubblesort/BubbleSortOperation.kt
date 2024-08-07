package com.example.algorithmvisualizer.domain.model.bubblesort

import com.example.algorithmvisualizer.domain.model.ISortOperation
import com.example.algorithmvisualizer.domain.model.Item

data class BubbleSortOperation(
    override val action: BubbleSortAction,
    override val indices: List<Int>,
    override val items: List<Item>,
) : ISortOperation