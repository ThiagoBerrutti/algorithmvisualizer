package com.example.algorithmvisualizer.data.algorithms

import android.util.Log
import com.example.algorithmvisualizer.domain.model.BubbleSortAction
import com.example.algorithmvisualizer.domain.model.BubbleSortIndicesHistory
import com.example.algorithmvisualizer.domain.model.Item
import com.example.algorithmvisualizer.domain.model.ItemStatus
import com.example.algorithmvisualizer.domain.model.OperationAndIndicesHistory
import com.example.algorithmvisualizer.domain.model.SnapshotManager
import com.example.algorithmvisualizer.domain.model.SortIndices
import com.example.algorithmvisualizer.domain.model.SortIterator
import com.example.algorithmvisualizer.domain.model.SortOperation
import com.example.algorithmvisualizer.domain.model.SortState
import com.example.algorithmvisualizer.domain.model.getIndices
import com.example.algorithmvisualizer.domain.model.setStatus
import com.example.algorithmvisualizer.domain.model.swap


class BubbleSortSortIterator(items: MutableList<Item>) : SortIterator<BubbleSortAction> {
    private var state: SortState = SortState(items.toMutableList())

    private val history =
        OperationAndIndicesHistory<BubbleSortAction, SortIndices>(indices = BubbleSortIndicesHistory())
    private val snapshotManager = SnapshotManager<List<Item>>(10)
    private var selectedIndices: MutableSet<Int> = mutableSetOf()

    override val getOperationSize = history::getOperationSize
    override var completedSortingAt: Int? = null

    override fun next(): SortOperation<BubbleSortAction>? {
        if (state.isSorted) return null

        // Verifica se existe historico a seguir
        val nextOperationIndex = history.getHistoryIndex() + 1
        val nextOperation = history.getOperation(nextOperationIndex)

        nextOperation?.let { nextOp ->
            val nextIndices = history.getIndices(nextOperationIndex)

            // Atribui indices anteriores ao state
            nextIndices?.let {
                state = state.copy(
                    subIndex = it.subIndex,
                    currentIndex = it.currentIndex,
                    returnPoint = it.returnPoint
                )
            }

            // Muda indice do historico de operations para o próximo
            history.incrementHistoryIndex()

            // Aplica mudanças
            applyOperation(state.items, nextOp)

            // Desseleciona itens anteriores
            removeSelectedItemsStatus(selectedIndices)

            // Seleciona itens atuais
            applySelectedItemsStatus(nextOp.indices)

            return nextOp
        }

        while (state.currentIndex < state.items.size) {
            // Identificador de ponto de retorno
            var checkpoint = 0

            if (state.subIndex < state.items.size - state.currentIndex - 1) {
                // Ponto de retorno 1: Comparação
                checkpoint = 1
                if (state.returnPoint < checkpoint) {
                    val index1 = state.subIndex
                    val index2 = state.subIndex + 1
                    val item1 = state.items[index1]
                    val item2 = state.items[index2]

                    // Desseleciona itens anteriores
                    removeSelectedItemsStatus(selectedIndices)

                    // Comparação
                    val compareOperation = SortOperation(
                        BubbleSortAction.Comparing,
                        listOf(index1, index2),
                        listOf(item1, item2)
                    )
                    history.addOperation(compareOperation)

                    // Marcar itens como selecionados
                    applySelectedItemsStatus(listOf(index1, index2))

                    // Salva estado após a comparação
                    snapshotManager.saveSnapshotIfNeeded(
                        history.getHistoryIndex(),
                        state.items.toList()
                    )

                    // Salva ponto de retorno
                    state = state.copy(returnPoint = checkpoint)

                    // Salva os indices no historico e retorna operação de comparação
                    history.addIndices(
                        operationIndex = history.getHistoryIndex(),
                        indices = state.getIndices()
                    )

                    return compareOperation
                }


                // Ponto de retorno 2: Troca (se necessário)
                if (state.items[state.subIndex].value > state.items[state.subIndex + 1].value) {
                    checkpoint = 2
                    if (state.returnPoint < checkpoint) {
                        val index1 = state.subIndex
                        val index2 = state.subIndex + 1
                        val item1 = state.items[index1]
                        val item2 = state.items[index2]

                        // Troca
                        val swapOperation = SortOperation(
                            BubbleSortAction.Swapping,
                            listOf(index1, index2),
                            listOf(item1, item2)
                        )
                        history.addOperation(swapOperation)

                        // Aplicar troca
                        applyOperation(state.items, swapOperation)

                        // Salva estado após a troca
                        snapshotManager.saveSnapshotIfNeeded(
                            history.getHistoryIndex(),
                            state.items.toList()
                        )

                        // Salva ponto de retorno
                        state = state.copy(returnPoint = checkpoint)

                        // Salva indices no historico e retorna operação de troca
                        history.addIndices(
                            operationIndex = history.getHistoryIndex(),
                            indices = state.getIndices()
                        )
                        return swapOperation
                    }
                }
                state = state.copy(subIndex = state.subIndex + 1)

            } else {
                state = state.copy(subIndex = 0, currentIndex = state.currentIndex + 1)
            }

            // Reseta ponto de retorno para o próximo loop
            state = state.copy(returnPoint = 0)
        }

        if (completedSortingAt == null) {
            completedSortingAt = history.getHistoryIndex()
            Log.d("COMPLETE_SORT_TEST", "$completedSortingAt")
        }
        state = state.copy(isSorted = true)
        return null
    }

    override fun prev(): SortOperation<BubbleSortAction>? {
        if (history.getHistoryIndex() < 0) return null

        val curOperation = history.getCurrentOperation()

        history.decrementHistoryIndex()
        val prevOperation = history.getCurrentOperation()

        state = state.copy(isSorted = false)

        val prevIndices = history.getIndices(history.getHistoryIndex())
        prevIndices?.let {
            state = state.copy(
                subIndex = it.subIndex,
                currentIndex = it.currentIndex,
                returnPoint = it.returnPoint
            )
        }

        curOperation?.let {
            applyOperation(state.items, it)
        }

        return prevOperation?.also { prevOp ->
            removeSelectedItemsStatus(selectedIndices)
            applySelectedItemsStatus(prevOp.indices)
        }
    }

    override fun setStep(step: Int): SortOperation<BubbleSortAction>? {
        val targetIndex = if (step - 1 < 0) {
            -1
        } else if (step >= history.getOperationSize()) {
            history.getOperationSize() - 1
        } else {
            step - 1
        }


        val (nearestSnapshotIndex, snapshot) = snapshotManager.getNearestSnapshot(
            targetIndex.coerceAtLeast(
                0
            )
        )
            ?: (0 to state.items.toMutableList())
        val resetItems = snapshot.toMutableList()


        var i = nearestSnapshotIndex
        do {
            removeSelectedItemsStatus(selectedIndices, resetItems)
            history.getOperation(i)?.let { op ->

                applyOperation(resetItems, op)
                selectedIndices.addAll(op.indices)

                history.getIndices(i)?.let {
                    state = state.copy(
                        subIndex = it.subIndex,
                        currentIndex = it.currentIndex,
                        returnPoint = it.returnPoint
                    )
                }
            }

            i++
        } while (i <= targetIndex)

        history.getOperation(i - 1)?.let { op ->
            applySelectedItemsStatus(op.indices, resetItems)
        }
        history.setHistoryIndex(targetIndex)

        val isSorted = targetIndex == completedSortingAt
        state = state.copy(items = resetItems, isSorted = isSorted)

        return history.getCurrentOperation()
    }

    override fun isSorted(): Boolean = completedSortingAt == history.getHistoryIndex()


    override fun getCurrentState(): List<Item> = state.items.toList()

    private fun applyOperation(
        list: MutableList<Item>,
        operation: SortOperation<BubbleSortAction>,
    ) {
        when (operation.action) {
            BubbleSortAction.Comparing -> {
                // No action needed for Comparing when applying operations
            }

            BubbleSortAction.Swapping -> {
                val (index1, index2) = operation.indices
                list.swap(index1, index2)
            }

            else -> {}
        }
    }

    private fun removeSelectedItemsStatus(
        indices: MutableSet<Int>,
        list: MutableList<Item> = state.items,
    ) {
        indices.forEach { list.setStatus(it, ItemStatus.Normal) }
        indices.clear()
    }

    private fun applySelectedItemsStatus(
        indices: List<Int>,
        list: MutableList<Item> = state.items,
    ) {
        indices.forEach { index ->
            list.setStatus(index, ItemStatus.Selected)
        }
        selectedIndices.clear()
        selectedIndices.addAll(indices)
    }
}
