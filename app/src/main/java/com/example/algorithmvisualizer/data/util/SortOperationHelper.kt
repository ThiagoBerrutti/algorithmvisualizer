package com.example.algorithmvisualizer.data.util

import com.example.algorithmvisualizer.domain.model.BubbleSortAction
import com.example.algorithmvisualizer.domain.model.BubbleSortAction.Comparing
import com.example.algorithmvisualizer.domain.model.BubbleSortAction.Complete
import com.example.algorithmvisualizer.domain.model.BubbleSortAction.Init
import com.example.algorithmvisualizer.domain.model.BubbleSortAction.Swapping
import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.QuickSortAction
import com.example.algorithmvisualizer.domain.model.QuickSortAction.Completed
import com.example.algorithmvisualizer.domain.model.QuickSortAction.FindingUnsorted
import com.example.algorithmvisualizer.domain.model.QuickSortAction.PartitionSorted
import com.example.algorithmvisualizer.domain.model.QuickSortAction.Partitioning
import com.example.algorithmvisualizer.domain.model.QuickSortAction.SelectingPivot
import com.example.algorithmvisualizer.domain.model.SortAction

//import com.example.algorithmvisualizer.domain.model.SortOperation


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

class SortOperationHelper2 {

    fun createMessage(operation: ISortOperation):String {
        return when (operation) {
            is QuickSortOperation -> {
                createQuickSortActionMessage(operation)
            }

            is BubbleSortOperation ->
                createBubbleSortActionMessage(operation)

            else -> "nem quick nem bubble WTF"
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
        return when (operation.action) {
            QuickSortAction.Comparing -> {
                "Comparando ${operation.items[0].value} e ${operation.items[1].value}"
            }

            Completed -> {
                "Ordenação concluida!"
            }

            FindingUnsorted -> "Procurando desordenados"
            PartitionSorted -> "Partição [${operation.indices[0]} até ${operation.indices[1]}] ordenada"
            Partitioning -> {
                val part1 = "[${operation.indices[0]} até ${operation.indices[1]}]"
                val part2 = if (operation.indices.size == 4) {
                    " e dos índices [${operation.indices[2]} até ${operation.indices[3]}]"
                } else {""}

                "Particionando do indice $part1$part2"
            }
            SelectingPivot -> "Selecionando pivo: ${operation.items[0].value} "
            QuickSortAction.Swapping -> {
                "${operation.items[0].value} é maior que ${operation.items[1].value}, então troca os itens de lugar"
        }

            QuickSortAction.ComparingLeftWithPivot -> "Selecionando índice esquerdo. ${operation.items[1].value} é menor que o pivot (${operation.items[0].value}). Avança um índice."
            QuickSortAction.ComparingRightWithPivot -> "Selecionando índice direito. ${operation.items[2].value} é maior que o pivot (${operation.items[0].value}). Volta um índice."
            QuickSortAction.LeftIndexSelected -> {"Índice esquerdo selecionado: ${operation.items[1].value} é maior ou igual ao pivot (${operation.items[0].value})"}
            QuickSortAction.RightIndexSelected -> {"Índice direito selecionado: ${operation.items[2].value} é menor ou igual ao pivot (${operation.items[0].value})"}
        }
    }

}

class SortOperationHelper {

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
                } else {""}

                "Partitioning from index $part1$part2"
            }
            SelectingPivot -> "Selecting pivot: ${operation.items[0].value}"
            QuickSortAction.Swapping -> {
                "${operation.items[0].value} is greater than ${operation.items[1].value}, so swap the items"
            }

            QuickSortAction.ComparingLeftWithPivot -> "Selecting left index. ${operation.items[1].value} is less than the pivot (${operation.items[0].value}). Move forward one index."
            QuickSortAction.ComparingRightWithPivot -> "Selecting right index. ${operation.items[2].value} is greater than the pivot (${operation.items[0].value}). Move back one index."
            QuickSortAction.LeftIndexSelected -> {"Left index selected: ${operation.items[1].value} is greater than or equal to the pivot (${operation.items[0].value})"}
            QuickSortAction.RightIndexSelected -> {"Right index selected: ${operation.items[2].value} is less than or equal to the pivot (${operation.items[0].value})"}
        }
    }
}
