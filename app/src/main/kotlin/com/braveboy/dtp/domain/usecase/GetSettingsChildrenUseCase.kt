package com.braveboy.dtp.domain.usecase

import com.braveboy.dtp.domain.model.SettingsNode
import com.braveboy.dtp.domain.repository.SettingsRepository

class GetSettingsChildrenUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(nodeId: String): List<SettingsNode> = repository.childrenOf(nodeId)
}
