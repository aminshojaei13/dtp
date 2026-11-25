package com.braveboy.dtp.data.repository

import com.braveboy.dtp.data.remote.datasource.SettingsRemoteDataSource
import com.braveboy.dtp.domain.model.SettingsNode
import com.braveboy.dtp.domain.repository.SettingsRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SettingsRepositoryImpl(
    private val remoteDataSource: SettingsRemoteDataSource
) : SettingsRepository {

    private var cachedRootNodes: List<SettingsNode>? = null
    private val mutex = Mutex()
    override suspend fun loadRoot(): List<SettingsNode> {
        return cachedRootNodes ?: mutex.withLock {
            cachedRootNodes ?: remoteDataSource.fetchSettingsFromJson().also {
                cachedRootNodes = it
            }
        }
    }
}
