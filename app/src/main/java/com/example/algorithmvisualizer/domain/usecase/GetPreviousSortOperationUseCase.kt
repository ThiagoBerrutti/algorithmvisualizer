package com.example.algorithmvisualizer.domain.usecase

import com.example.algorithmvisualizer.domain.model.ISortOperation
import com.example.algorithmvisualizer.domain.model.SortAction
import com.example.algorithmvisualizer.domain.model.SortIterator

class GetPreviousSortOperationUseCase {
    suspend fun execute(sortIterator: SortIterator<SortAction>): ISortOperation? {
        return sortIterator.prev()
    }
}