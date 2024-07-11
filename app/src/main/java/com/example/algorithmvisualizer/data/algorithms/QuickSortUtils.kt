package com.example.algorithmvisualizer.data.algorithms

import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.ItemStatus


fun setItemsStatusAt(list:MutableList<Item>, indices: Iterable<Int>, status:ItemStatus, predicate: ((Item) ->Boolean)? = null) {
    indices.forEach {
        val shouldApplyStatus = predicate?.invoke(list[it]) == true
        if (shouldApplyStatus) {
            list[it] = list[it].copy(status = status)
        }
    }
}

//fun setItemsStatusAt(list:MutableList<Item>, range: IntRange, status:ItemStatus,predicate: ((Item) ->Boolean)? = null) {
//    setItemsStatusAt(list,range.toList(), status, predicate)
//}
