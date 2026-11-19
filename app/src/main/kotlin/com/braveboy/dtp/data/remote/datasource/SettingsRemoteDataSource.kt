package com.braveboy.dtp.data.remote.datasource

import com.braveboy.dtp.domain.model.SettingsNode

interface SettingsRemoteDataSource {
    suspend fun fetchSettingsFromJson(): List<SettingsNode>
}