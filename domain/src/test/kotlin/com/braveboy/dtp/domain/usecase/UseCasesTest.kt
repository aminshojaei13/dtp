package com.braveboy.dtp.domain.usecase

import com.braveboy.dtp.domain.model.SettingsNode
import com.braveboy.dtp.domain.repository.SettingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class UseCasesTest {

    private val nodes = listOf(SettingsNode(id = "display", title = "Display"))

    @Test
    fun getRootSettingsUseCase_returnsRepositoryRootNodes() = runTest {
        val repository = object : SettingsRepository {
            override suspend fun loadRoot(): List<SettingsNode> = nodes
        }

        assertEquals(nodes, GetRootSettingsUseCase(repository).invoke())
    }
}
