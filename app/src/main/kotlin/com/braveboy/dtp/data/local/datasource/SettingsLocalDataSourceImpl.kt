package com.braveboy.dtp.data.local.datasource

import com.braveboy.dtp.data.remote.datasource.SettingsRemoteDataSource
import com.braveboy.dtp.domain.model.SettingsNode
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SettingsLocalDataSourceImpl(
    private val assetDataSource: SettingsRemoteDataSource
) : SettingsLocalDataSource {
    private var cachedRootNodes: List<SettingsNode>? = null
    private val mutex = Mutex()

    private suspend fun ensureLoaded(): List<SettingsNode> {
        return cachedRootNodes ?: mutex.withLock {
            cachedRootNodes ?: assetDataSource.fetchSettingsFromJson().also {
                cachedRootNodes = it
            }
        }
    }

    override suspend fun getRootNodes(): List<SettingsNode> {
        return ensureLoaded()
    }

    override suspend fun getChildrenOf(parentId: String): List<SettingsNode> {
        val root = ensureLoaded()
        return findNodeInTree(root, parentId)?.children.orEmpty()
    }

    override suspend fun findNode(nodeId: String): SettingsNode? {
        val root = ensureLoaded()
        return findNodeInTree(root, nodeId)
    }

    private fun findNodeInTree(nodes: List<SettingsNode>, id: String): SettingsNode? {
        nodes.forEach { node ->
            if (node.id == id) return node
            findNodeInTree(node.children, id)?.let { return it }
        }
        return null
    }
}
