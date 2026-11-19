package com.braveboy.dtp.ui.setting

sealed interface SettingsEffect {
    data object ExitScreen : SettingsEffect
}