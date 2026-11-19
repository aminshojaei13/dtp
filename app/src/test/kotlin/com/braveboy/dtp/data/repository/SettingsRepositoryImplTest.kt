package com.braveboy.dtp.data.repository

import com.braveboy.dtp.data.local.datasource.SettingsLocalDataSource
import com.braveboy.dtp.domain.model.SettingsNode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsRepositoryImplTest {

    private val fakeNodes = listOf(
        SettingsNode(id = "network", title = "Network"),
        SettingsNode(id = "display", title = "Display")
    )

    private val fakeLocalDataSource = object : SettingsLocalDataSource {
        override suspend fun getRootNodes(): List<SettingsNode> = fakeNodes

        override suspend fun getChildrenOf(parentId: String): List<SettingsNode> {
            return if (parentId == "network") listOf(SettingsNode(id = "wifi", title = "Wi-Fi")) else emptyList()
        }

        override suspend fun findNode(nodeId: String): SettingsNode? {
            return if (nodeId == "network") fakeNodes[0] else null
        }
    }

    private val repository = SettingsRepositoryImpl(fakeLocalDataSource)

    @Test
    fun loadRoot_delegatesToLocalDataSource() = runTest {
        val result = repository.loadRoot()
        assertEquals(fakeNodes, result)
    }

    @Test
    fun childrenOf_delegatesToLocalDataSource() = runTest {
        val result = repository.childrenOf("network")
        assertEquals(1, result.size)
        assertEquals("wifi", result[0].id)
    }

    @Test
    fun findNode_delegatesToLocalDataSource() = runTest {
        val node = repository.findNode("network")
        assertEquals("network", node?.id)

        val nonExistent = repository.findNode("unknown")
        assertNull(nonExistent)
    }
}
