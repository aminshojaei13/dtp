package com.braveboy.dtp.domain.usecase

import com.braveboy.dtp.domain.model.SettingsNode
import com.braveboy.dtp.domain.repository.SettingsRepository

class FindSettingsNodeUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(nodeId: String): SettingsNode? = repository.findNode(nodeId)
}
