package com.braveboy.dtp.data.local.datasource

import com.braveboy.dtp.domain.model.SettingsNode

interface SettingsLocalDataSource {
    suspend fun getRootNodes(): List<SettingsNode>
    suspend fun getChildrenOf(parentId: String): List<SettingsNode>
    suspend fun findNode(nodeId: String): SettingsNode?
}