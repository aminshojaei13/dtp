package com.braveboy.dtp.domain.model

import com.braveboy.twopane.model.TwoPaneNavNode
import kotlinx.serialization.Serializable

@Serializable
data class SettingsNode(
    override val id: String,
    override val title: String,
    override val summary: String = "",
    override val value: String? = null,
    override val children: List<SettingsNode> = emptyList()
) : TwoPaneNavNode
