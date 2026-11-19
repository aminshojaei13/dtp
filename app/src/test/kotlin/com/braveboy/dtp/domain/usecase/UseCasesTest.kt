package com.braveboy.dtp.domain.usecase

import com.braveboy.dtp.domain.model.SettingsNode
import com.braveboy.dtp.domain.repository.SettingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UseCasesTest {

    private val fakeNodes = listOf(
        SettingsNode(id = "root", title = "Root")
    )

    private val fakeRepository = object : SettingsRepository {
        override suspend fun loadRoot(): List<SettingsNode> = fakeNodes
        override suspend fun childrenOf(nodeId: String): List<SettingsNode> = emptyList()
        override suspend fun findNode(nodeId: String): SettingsNode? = fakeNodes.firstOrNull { it.id == nodeId }
    }

    @Test
    fun getRootSettingsUseCase_invokesRepositoryLoadRoot() = runTest {
        val useCase = GetRootSettingsUseCase(fakeRepository)
        val result = useCase()
        assertEquals(fakeNodes, result)
    }
}
