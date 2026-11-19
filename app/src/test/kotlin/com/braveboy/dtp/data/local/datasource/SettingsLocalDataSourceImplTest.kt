package com.braveboy.dtp.data.local.datasource

import com.braveboy.dtp.data.remote.datasource.SettingsRemoteDataSource
import com.braveboy.dtp.domain.model.SettingsNode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SettingsLocalDataSourceImplTest {

    private val fakeNodes = listOf(
        SettingsNode(
            id = "network",
            title = "Network",
            summary = "Network settings",
            children = listOf(
                SettingsNode(
                    id = "wifi",
                    title = "Wi-Fi",
                    summary = "Wi-Fi settings",
                    children = listOf(
                        SettingsNode(id = "signal", title = "Signal", value = "90%")
                    )
                )
            )
        ),
        SettingsNode(
            id = "display",
            title = "Display",
            summary = "Display settings"
        )
    )

    private val fakeRemoteDataSource = object : SettingsRemoteDataSource {
        var fetchCallCount = 0
        override suspend fun fetchSettingsFromJson(): List<SettingsNode> {
            fetchCallCount++
            return fakeNodes
        }
    }

    private val dataSource = SettingsLocalDataSourceImpl(fakeRemoteDataSource)

    @Test
    fun getRootNodes_returnsRootNodesAndCachesResult() = runTest {
        val nodes = dataSource.getRootNodes()
        assertEquals(2, nodes.size)
        assertEquals("network", nodes[0].id)
        assertEquals("display", nodes[1].id)

        // second time
        val cachedNodes = dataSource.getRootNodes()
        assertEquals(2, cachedNodes.size)
        assertEquals(1, fakeRemoteDataSource.fetchCallCount)
    }

    @Test
    fun getChildrenOf_existingNode_returnsChildren() = runTest {
        val children = dataSource.getChildrenOf("network")
        assertEquals(1, children.size)
        assertEquals("wifi", children[0].id)
    }

    @Test
    fun getChildrenOf_leafNode_returnsEmptyList() = runTest {
        val children = dataSource.getChildrenOf("signal")
        assertEquals(0, children.size)
    }

    @Test
    fun getChildrenOf_nonExistentNode_returnsEmptyList() = runTest {
        val children = dataSource.getChildrenOf("unknown")
        assertEquals(0, children.size)
    }

    @Test
    fun findNode_existingNestedNode_returnsNode() = runTest {
        val node = dataSource.findNode("signal")
        assertNotNull(node)
        assertEquals("signal", node?.id)
        assertEquals("90%", node?.value)
    }

    @Test
    fun findNode_nonExistentNode_returnsNull() = runTest {
        val node = dataSource.findNode("unknown")
        assertNull(node)
    }
}
