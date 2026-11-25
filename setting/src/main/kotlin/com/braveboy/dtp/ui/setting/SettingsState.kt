package com.braveboy.dtp.ui.setting

import com.braveboy.dtp.domain.model.SettingsNode

data class SettingsState(
    val isLoading: Boolean = true,
    val path: List<SettingsNode> = emptyList(),
    val mainRows: List<SettingsNode> = emptyList(),
    val focusedIndex: Int = 0,
    val preview: SettingsNode? = null,
    val error: String? = null
)
