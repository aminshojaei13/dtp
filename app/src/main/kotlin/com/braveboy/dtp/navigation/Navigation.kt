package com.braveboy.dtp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
data class SettingsList(val depth: Int, val title: String) : NavKey

@Serializable
data class SettingsPreview(
    val nodeId: String,
): NavKey