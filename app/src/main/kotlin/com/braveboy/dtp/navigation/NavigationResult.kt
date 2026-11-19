package com.braveboy.dtp.navigation

sealed interface NavigationResult {
    data class Changed(val state: SettingsNavigationState) : NavigationResult
    data object NoOp : NavigationResult
    data object Exit : NavigationResult
}