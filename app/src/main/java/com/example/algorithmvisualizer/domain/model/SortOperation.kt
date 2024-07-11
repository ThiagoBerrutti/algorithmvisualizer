package com.example.algorithmvisualizer.domain.model

//interface SortOperation<out TAction: SortAction> {
//    val action: TAction
//    val indices: List<Int>
//    val items: List<Item>
//}
//
//data class SortOperationImpl<out T: SortAction>(
//    override val action: T,
//    override val indices: List<Int>,
//    override val items: List<Item>
//) : SortOperation<T>

data class SortOperation<out T: SortAction>(
     val action: T,
     val indices: List<Int>,
     val items: List<Item>
)