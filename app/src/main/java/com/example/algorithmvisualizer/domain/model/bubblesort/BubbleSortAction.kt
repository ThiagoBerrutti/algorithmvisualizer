package com.example.algorithmvisualizer.domain.model.bubblesort

import com.example.algorithmvisualizer.domain.model.SortAction

enum class BubbleSortAction : SortAction {
    Init, Comparing, Swapping, Complete
}