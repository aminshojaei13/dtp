package com.braveboy.dtp.data.repository

import com.braveboy.dtp.data.local.datasource.SettingsLocalDataSource
import com.braveboy.dtp.domain.model.SettingsNode
import com.braveboy.dtp.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override suspend fun loadRoot(): List<SettingsNode> {
        return localDataSource.getRootNodes()
    }

    override suspend fun childrenOf(nodeId: String): List<SettingsNode> {
        return localDataSource.getChildrenOf(nodeId)
    }

    override suspend fun findNode(nodeId: String): SettingsNode? {
        return localDataSource.findNode(nodeId)
    }
}
