package com.braveboy.dtp.domain.repository

import com.braveboy.dtp.domain.model.SettingsNode

interface SettingsRepository {
    suspend fun loadRoot(): List<SettingsNode>
}
