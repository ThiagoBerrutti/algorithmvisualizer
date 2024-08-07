package com.example.algorithmvisualizer.domain.model.quicksort

import com.example.algorithmvisualizer.domain.model.SortAction

sealed class QuickSortAction : SortAction {
    data object Comparing : QuickSortAction()
    data object Swapping : QuickSortAction()
    data object SelectingPivot : QuickSortAction()
    data object ComparingLeftWithPivot : QuickSortAction()
    data object LeftIndexSelected : QuickSortAction()
    data object ComparingRightWithPivot : QuickSortAction()
    data object RightIndexSelected : QuickSortAction()
    data object FindingUnsorted : QuickSortAction()
    data object Partitioning : QuickSortAction()
    data object PartitionSorted : QuickSortAction()
    data object Completed : QuickSortAction()
}