package com.example.algorithmvisualizer.presentation.utils

import com.example.algorithmvisualizer.domain.model.Item
import java.util.UUID


//private val ITEMS_VALUES = listOf(15, 4, 26, 10, 22, 35, 14, 3)
private val ITEMS_VALUES_80 = listOf(
    45, 11, 36, 16, 6, 19, 27, 10, 34, 15, 38, 32, 26, 47, 13, 40, 20, 39, 29, 50,
    24, 30, 21, 4, 44, 17, 9, 28, 35, 3, 25, 42, 8, 2, 46, 33, 5, 41, 22, 37, 48,
    31, 7, 14, 12, 18, 23, 49, 43, 30, 25, 38, 9, 20, 14, 27, 3, 32, 47, 15, 41, 6, 50,
    24, 17, 4, 34, 13, 10, 2, 11, 35, 8, 21, 28, 45, 7, 16, 33, 5
)

fun generateItems(count: Int): List<Item> {
    val items = mutableListOf<Item>()
    for (n in 1..count) {
        val value = (2..50).random();
        val id = UUID.randomUUID().toString()
        val item = Item(id, value, id)
        items.add(item)
    }

    return items.toList()
}

fun generateItemsFrom(values: List<Int>): List<Item> {
    val items = mutableListOf<Item>()
//    for (n in values) {
//        val value = (2..50).random();
//        val id = UUID.randomUUID().toString()
//        val item = Item(id, value, id)
//        items.add(item)
//    }
    for (value in values) {
        val id = UUID.randomUUID().toString()
        val item = Item(id, value, id)
        items.add(item)
    }

    return items.toList()
}


fun generateStaticItems(size: Int = ITEMS_VALUES_80.size): List<Item> =
    ITEMS_VALUES_80
        .take(size.coerceIn(1, ITEMS_VALUES_80.size))
        .map { value ->
            val id = UUID.randomUUID().toString()
            Item(id, value, id)
        }

fun generateRandomValues(count: Int = 8, min: Int = 2, max: Int = 50): List<Int> {
    return (1..count.coerceAtLeast(1)).map { (min..max).random() }
}


data class KeyedValue<out K, out V>(val key: K, val value: V)

fun <T, K> T.toKeyedValue(key: K) = KeyedValue(key, this)
fun <T> T.toKeyedValue(key: String = UUID.randomUUID().toString()) = KeyedValue(key, this)


