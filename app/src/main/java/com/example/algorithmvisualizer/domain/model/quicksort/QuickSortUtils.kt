package com.example.algorithmvisualizer.domain.model.quicksort

import com.example.algorithmvisualizer.domain.model.Item

typealias SubListIndices = Pair<Int, Int>

object QuickSortUtils {
    fun partitionOperationFactory(
        arr: List<Item>,
        indicesLeft: SubListIndices? = null,
        indicesRight: SubListIndices? = null,
    ): QuickSortOperation {
        val indices: List<Int> = indicesLeft?.toList().orEmpty().toMutableList() +
                indicesRight?.toList().orEmpty().toMutableList()

        return QuickSortOperation(
            QuickSortAction.Partitioning,
            indices,
            indices.mapNotNull { arr.elementAtOrNull(it) }
        )
    }

    fun partitionSortedOperationFactory(arr: List<Item>, leftIndex: Int, rightIndex: Int) =
        QuickSortOperation(
            QuickSortAction.PartitionSorted,
            listOf(leftIndex, rightIndex),
            listOfNotNull(
                arr.elementAtOrNull(leftIndex),
                arr.elementAtOrNull(rightIndex)
            )
        )

    fun swapOperationFactory(
        arr: List<Item>,
        leftIndex: Int,
        rightIndex: Int,
    ): QuickSortOperation {
        return QuickSortOperation(
            QuickSortAction.Swapping,
            listOf(leftIndex, rightIndex),
            listOfNotNull(
                arr.elementAtOrNull(leftIndex)?.copy(),
                arr.elementAtOrNull(rightIndex)?.copy()
            )
        )
    }


    fun comparingLeftOperationFactory(
        arr: List<Item>,
        pivotIndex: Int,
        leftIndex: Int,
        rightIndex: Int,
    ) = QuickSortOperation(
        QuickSortAction.ComparingLeftWithPivot,
        listOf(pivotIndex, leftIndex, rightIndex),
        listOfNotNull(
            arr.elementAtOrNull(pivotIndex),
            arr.elementAtOrNull(leftIndex),
            arr.elementAtOrNull(rightIndex),
        )
    )

    fun comparingRightOperationFactory(
        arr: List<Item>,
        pivotIndex: Int,
        leftIndex: Int,
        rightIndex: Int,
    ) =
        QuickSortOperation(
            QuickSortAction.ComparingRightWithPivot,
            listOf(pivotIndex, leftIndex, rightIndex),
            listOfNotNull(
                arr.elementAtOrNull(pivotIndex),
                arr.elementAtOrNull(leftIndex),
                arr.elementAtOrNull(rightIndex),
            )
        )

    fun leftSelectedOperationFactory(
        arr: List<Item>,
        pivotIndex: Int,
        leftIndex: Int,
        rightIndex: Int,
    ) =
        QuickSortOperation(
            QuickSortAction.LeftIndexSelected,
            listOf(pivotIndex, leftIndex, rightIndex),
            listOfNotNull(
                arr.elementAtOrNull(pivotIndex),
                arr.elementAtOrNull(leftIndex),
                arr.elementAtOrNull(rightIndex),
            )
        )

    fun rightSelectedOperationFactory(
        arr: List<Item>,
        pivotIndex: Int,
        leftIndex: Int,
        rightIndex: Int,
    ) =
        QuickSortOperation(
            QuickSortAction.RightIndexSelected,
            listOf(pivotIndex, leftIndex, rightIndex),
            listOfNotNull(
                arr.elementAtOrNull(pivotIndex),
                arr.elementAtOrNull(leftIndex),
                arr.elementAtOrNull(rightIndex),
            )
        )
}