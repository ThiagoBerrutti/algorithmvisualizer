package com.example.algorithmvisualizer.domain.model

data class Item(
    val id: String,
    val value: Int,
    val label: String,
    val status:ItemStatus = ItemStatus.Normal
)

enum class ItemStatus {
    Normal, Selected, Static, Partition,
}