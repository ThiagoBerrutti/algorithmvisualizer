package com.example.algorithmvisualizer.domain.util

import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.ItemStatus

fun <T> MutableList<T>.swap(index1: Int, index2: Int): MutableList<T> {
    val temp = this[index1]
    this[index1] = this[index2]
    this[index2] = temp
    return this
}

fun MutableList<Item>.setStatus(index: Int, status: ItemStatus) {
    this[index] = this[index].copy(status = status)
}