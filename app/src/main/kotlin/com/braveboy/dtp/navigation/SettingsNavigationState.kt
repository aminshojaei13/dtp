package com.braveboy.dtp.navigation

import com.braveboy.dtp.domain.model.SettingsNode

data class SettingsNavigationState(
    val path: List<SettingsNode>,
    val focusedRowId: String?,
    val focusedIndex: Int = 0
) {
    val currentRows: List<SettingsNode> get() = path.lastOrNull()?.children.orEmpty()
    val selectedRow: SettingsNode? get() = currentRows.getOrNull(focusedIndex)
    val depth: Int get() = path.size
}