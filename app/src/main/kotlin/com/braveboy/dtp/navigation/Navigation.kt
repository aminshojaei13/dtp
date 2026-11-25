package com.braveboy.dtp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
data object SettingsMain : NavKey

@Serializable
data class SettingsPreview(
    val nodeId: String,
): NavKey