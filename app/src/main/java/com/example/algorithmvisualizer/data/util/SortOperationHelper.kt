package com.example.algorithmvisualizer.data.util

import com.example.algorithmvisualizer.domain.model.BubbleSortAction
import com.example.algorithmvisualizer.domain.model.BubbleSortAction.Comparing
import com.example.algorithmvisualizer.domain.model.BubbleSortAction.Complete
import com.example.algorithmvisualizer.domain.model.BubbleSortAction.Init
import com.example.algorithmvisualizer.domain.model.BubbleSortAction.Swapping
import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.QuickSortAction
import com.example.algorithmvisualizer.domain.model.SortAction
import com.example.algorithmvisualizer.domain.model.SortOperation


interface ISortOperation {
    val action: SortAction
    val indices: List<Int>
    val items: List<Item>
}

data class BubbleSortOperation(
    override val action: BubbleSortAction,
    override val indices: List<Int>,
    override val items: List<Item>,
) : ISortOperation

data class QuickSortOperation(
    override val action: QuickSortAction,
    override val indices: List<Int>,
    override val items: List<Item>,
) : ISortOperation

// Temporario, já que nao quero ter que refatorar todos os locais com SortOperation
fun transformOp(operation: SortOperation<SortAction>): ISortOperation {
    return if (operation.action is QuickSortAction) {
        QuickSortOperation(
            action = operation.action,
            items = operation.items,
            indices = operation.indices
        )
    } else {
        BubbleSortOperation(
            action = operation.action as BubbleSortAction,
            items = operation.items,
            indices = operation.indices
        )
    }
}

class SortOperationHelper {
    fun createMessage(operation: ISortOperation) {
        val text: String = when (operation) {
            is QuickSortOperation -> {
                createQuickSortActionMessage(operation)
            }
            is BubbleSortOperation ->
                createBubbleSortActionMessage(operation)
            else -> ""
        }
    }

    private fun createBubbleSortActionMessage(operation: BubbleSortOperation): String {
        return when (operation.action) {
            Init -> {
                "Vamos ordenar essa lista"
            }

            Comparing -> {
                "Comparando ${operation.items[0].value} com ${operation.items[1].value}"
            }

            Swapping -> {
                "${operation.items[0].value} é maior que ${operation.items[1].value}, então troca os itens de lugar"
            }

            Complete -> {
                "Ordenação concluída!"
            }

        }
    }

    private fun createQuickSortActionMessage(operation: QuickSortOperation): String {
        return ""
//        when (operation.action){
//            QuickSortAction.Comparing -> {
//                "Comparando ${operation.items[0].value} e ${operation.items[1].value}"
//            }
//            QuickSortAction.Completed -> {}
//            QuickSortAction.FindingUnsorted -> {}
//            QuickSortAction.PartitionSorted -> {}
//            QuickSortAction.Partitioning -> {}
//            QuickSortAction.SelectingPivot -> {}
//            QuickSortAction.Swapping -> {}
//        }
    }

}