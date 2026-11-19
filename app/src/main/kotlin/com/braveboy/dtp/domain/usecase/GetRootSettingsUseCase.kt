package com.braveboy.dtp.domain.usecase

import com.braveboy.dtp.domain.model.SettingsNode
import com.braveboy.dtp.domain.repository.SettingsRepository

class GetRootSettingsUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(): List<SettingsNode> = repository.loadRoot()
}
