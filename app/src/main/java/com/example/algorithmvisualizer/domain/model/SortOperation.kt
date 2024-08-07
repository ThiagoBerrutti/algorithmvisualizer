package com.example.algorithmvisualizer.domain.model

interface ISortOperation {
    val action: SortAction
    val indices: List<Int>
    val items: List<Item>
}

