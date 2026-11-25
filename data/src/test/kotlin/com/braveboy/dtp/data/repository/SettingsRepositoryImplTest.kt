package com.braveboy.dtp.data.repository

import com.braveboy.dtp.data.remote.datasource.SettingsRemoteDataSource
import com.braveboy.dtp.domain.model.SettingsNode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsRepositoryImplTest {

    private val nodes = listOf(SettingsNode(id = "network", title = "Network"))

    private class CountingDataSource(private val nodes: List<SettingsNode>) : SettingsRemoteDataSource {
        var fetchCount = 0
        override suspend fun fetchSettingsFromJson(): List<SettingsNode> {
            fetchCount++
            return nodes
        }
    }

    @Test
    fun loadRoot_returnsDataFromDataSource() = runTest {
        val repository = SettingsRepositoryImpl(CountingDataSource(nodes))

        assertEquals(nodes, repository.loadRoot())
    }

    @Test
    fun loadRoot_cachesResultAfterFirstCall() = runTest {
        val dataSource = CountingDataSource(nodes)
        val repository = SettingsRepositoryImpl(dataSource)

        repository.loadRoot()
        repository.loadRoot()

        assertEquals(1, dataSource.fetchCount)
    }
}
