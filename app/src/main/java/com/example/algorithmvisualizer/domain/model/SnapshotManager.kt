package com.example.algorithmvisualizer.domain.model

class SnapshotManager<T>(private val snapshotInterval: Int) {
    private val snapshots = mutableMapOf<Int, T>()

    fun saveSnapshotIfNeeded(index: Int, items: T) {
        if (index % snapshotInterval == 0) {
            snapshots[index] = items
        }
    }

    fun getNearestSnapshot(index: Int): Pair<Int, T>? {
        val nearestSnapshotIndex = snapshots.keys.filter { it <= index }.maxOrNull()
        return nearestSnapshotIndex?.let {
            it to snapshots[it]!!
        }
    }
}