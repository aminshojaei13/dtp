package com.braveboy.dtp.domain.repository

import com.braveboy.dtp.domain.model.SettingsNode

interface SettingsRepository {
    suspend fun loadRoot(): List<SettingsNode>
    suspend fun childrenOf(nodeId: String): List<SettingsNode>
    suspend fun findNode(nodeId: String): SettingsNode?
}
