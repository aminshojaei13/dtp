package com.braveboy.dtp.navigation

class SettingsNavigator {
    fun move(state: SettingsNavigationState, delta: Int): NavigationResult {
        val rows = state.currentRows
        if (rows.isEmpty()) return NavigationResult.NoOp
        val next = (state.focusedIndex + delta).coerceIn(0, rows.lastIndex)
        if (next == state.focusedIndex) return NavigationResult.NoOp
        return NavigationResult.Changed(state.copy(focusedIndex = next, focusedRowId = rows[next].id))
    }

    fun forward(state: SettingsNavigationState): NavigationResult {
        val selected = state.selectedRow ?: return NavigationResult.NoOp
        if (selected.children.isEmpty()) return NavigationResult.NoOp
        val child = selected.children.firstOrNull() ?: return NavigationResult.NoOp
        return NavigationResult.Changed(
            state.copy(path = state.path + selected, focusedRowId = child.id, focusedIndex = 0)
        )
    }

    fun right(state: SettingsNavigationState): NavigationResult {
        if (state.path.size <= 1) return NavigationResult.NoOp
        val newPath = state.path.dropLast(1)
        val parentRows = newPath.last().children
        val returningId = state.path.last().id
        val index = parentRows.indexOfFirst { it.id == returningId }.coerceAtLeast(0)
        return NavigationResult.Changed(
            state.copy(path = newPath, focusedRowId = returningId, focusedIndex = index)
        )
    }

    fun back(state: SettingsNavigationState): NavigationResult {
        if (state.path.size <= 1) return NavigationResult.Exit
        val newPath = state.path.dropLast(1)
        val parentRows = newPath.last().children
        val returningId = state.path.last().id
        val index = parentRows.indexOfFirst { it.id == returningId }.coerceAtLeast(0)
        return NavigationResult.Changed(
            state.copy(path = newPath, focusedRowId = returningId, focusedIndex = index)
        )
    }

    fun focus(state: SettingsNavigationState, index: Int): NavigationResult {
        val rows = state.currentRows
        if (index !in rows.indices || index == state.focusedIndex) return NavigationResult.NoOp
        return NavigationResult.Changed(state.copy(focusedIndex = index, focusedRowId = rows[index].id))
    }
}
