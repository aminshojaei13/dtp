package com.braveboy.dtp.ui.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import com.braveboy.dtp.domain.model.SettingsNode
import com.braveboy.dtp.ui.theme.DTPTheme

@Preview
@Composable
private fun SettingsScreenPreview() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        DTPTheme {
            SettingsScreen(
                state = SampleSettingsState
            )
        }
    }
}

private val SampleSettingsNodes = listOf(
    SettingsNode(
        id = "network",
        title = "Network & Internet",
        summary = "Wi-Fi, Ethernet, Data usage",
        children = listOf(
            SettingsNode(id = "wifi", title = "Wi-Fi", value = "Connected"),
            SettingsNode(id = "ethernet", title = "Ethernet", value = "Not connected")
        )
    ),
    SettingsNode(
        id = "display",
        title = "Display & Sound",
        summary = "Resolution, Refresh rate, Audio output",
        children = listOf(
            SettingsNode(id = "resolution", title = "Resolution", value = "4K Ultra HD"),
            SettingsNode(
                id = "refresh_rate",
                title = "Match content frame rate",
                value = "Seamless"
            )
        )
    ),
    SettingsNode(
        id = "apps",
        title = "Apps",
        summary = "Permissions, Default apps",
        children = listOf(
            SettingsNode(id = "permissions", title = "App permissions"),
            SettingsNode(id = "special_access", title = "Special app access")
        )
    )
)

private val SampleSettingsState = SettingsState(
    isLoading = false,
    path = listOf(SettingsNode(id = "root", title = "Settings")),
    mainRows = SampleSettingsNodes,
    focusedIndex = 0,
    preview = SampleSettingsNodes.firstOrNull()
)