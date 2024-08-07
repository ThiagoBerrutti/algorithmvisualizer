package com.example.algorithmvisualizer.domain.util

import com.example.algorithmvisualizer.domain.model.bubblesort.BubbleSortAction.Comparing
import com.example.algorithmvisualizer.domain.model.bubblesort.BubbleSortAction.Complete
import com.example.algorithmvisualizer.domain.model.bubblesort.BubbleSortAction.Init
import com.example.algorithmvisualizer.domain.model.bubblesort.BubbleSortAction.Swapping
import com.example.algorithmvisualizer.domain.model.bubblesort.BubbleSortOperation
import com.example.algorithmvisualizer.domain.model.ISortOperation
import com.example.algorithmvisualizer.domain.model.quicksort.QuickSortAction
import com.example.algorithmvisualizer.domain.model.quicksort.QuickSortAction.Completed
import com.example.algorithmvisualizer.domain.model.quicksort.QuickSortAction.FindingUnsorted
import com.example.algorithmvisualizer.domain.model.quicksort.QuickSortAction.PartitionSorted
import com.example.algorithmvisualizer.domain.model.quicksort.QuickSortAction.Partitioning
import com.example.algorithmvisualizer.domain.model.quicksort.QuickSortAction.SelectingPivot
import com.example.algorithmvisualizer.domain.model.quicksort.QuickSortOperation

object SortOperationInfoGenerator {
    fun createMessage(operation: ISortOperation): String {
        return when (operation) {
            is QuickSortOperation -> {
                createQuickSortActionMessage(operation)
            }

            is BubbleSortOperation ->
                createBubbleSortActionMessage(operation)

            else -> "neither quick nor bubble WTF"
        }
    }

    private fun createBubbleSortActionMessage(operation: BubbleSortOperation): String {
        return when (operation.action) {
            Init -> {
                "Let's sort this list"
            }

            Comparing -> {
                "Comparing ${operation.items[0].value} with ${operation.items[1].value}"
            }

            Swapping -> {
                "${operation.items[0].value} is greater than ${operation.items[1].value}, so swap the items"
            }

            Complete -> {
                "Sorting complete!"
            }
        }
    }

    private fun createQuickSortActionMessage(operation: QuickSortOperation): String {
        return when (operation.action) {
            QuickSortAction.Comparing -> {
                "Comparing ${operation.items[0].value} and ${operation.items[1].value}"
            }

            Completed -> {
                "Sorting complete!"
            }

            FindingUnsorted -> "Finding unsorted elements"
            PartitionSorted -> "Partition [${operation.indices[0]} to ${operation.indices[1]}] sorted"
            Partitioning -> {
                val part1 = "[${operation.indices[0]} to ${operation.indices[1]}]"
                val part2 = if (operation.indices.size == 4) {
                    " and indices [${operation.indices[2]} to ${operation.indices[3]}]"
                } else {
                    ""
                }

                "Partitioning from index $part1$part2"
            }

            SelectingPivot -> "Selecting pivot: ${operation.items[0].value}"
            QuickSortAction.Swapping -> {
                "${operation.items[0].value} is greater than ${operation.items[1].value}, so swap the items"
            }

            QuickSortAction.ComparingLeftWithPivot -> "Selecting left index. ${operation.items[1].value} is less than the pivot (${operation.items[0].value}). Move forward one index."
            QuickSortAction.ComparingRightWithPivot -> "Selecting right index. ${operation.items[2].value} is greater than the pivot (${operation.items[0].value}). Move back one index."
            QuickSortAction.LeftIndexSelected -> {
                "Left index selected: ${operation.items[1].value} is greater than or equal to the pivot (${operation.items[0].value})"
            }

            QuickSortAction.RightIndexSelected -> {
                "Right index selected: ${operation.items[2].value} is less than or equal to the pivot (${operation.items[0].value})"
            }
        }
    }
}
