package com.example.algorithmvisualizer.domain.model

interface IndicesHistory<TIndices> {
    fun addIndices(operationIndex: Int, indices: TIndices): TIndices?
    fun getIndices(operationIndex: Int): TIndices?
}