package com.braveboy.dtp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SettingsNode(
    val id: String,
    val title: String,
    val summary: String = "",
    val value: String? = null,
    val children: List<SettingsNode> = emptyList()
)