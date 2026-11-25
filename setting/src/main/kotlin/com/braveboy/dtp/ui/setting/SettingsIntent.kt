package com.braveboy.dtp.ui.setting

sealed interface SettingsIntent {
    data object Up : SettingsIntent
    data object Down : SettingsIntent
    data object Forward : SettingsIntent
    data object Right : SettingsIntent
    data object Back : SettingsIntent
    data class FocusRow(val index: Int) : SettingsIntent
    data object Retry : SettingsIntent
}