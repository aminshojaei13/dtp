package com.braveboy.dtp.data.remote.datasource

import android.content.Context
import com.braveboy.dtp.domain.model.SettingsNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class SettingsRemoteDataSourceImpl(
    private val context: Context,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : SettingsRemoteDataSource {

    override suspend fun fetchSettingsFromJson(): List<SettingsNode> = withContext(Dispatchers.IO) {
        val jsonString = context.assets.open("settings.json").bufferedReader().use { it.readText() }
        json.decodeFromString<List<SettingsNode>>(jsonString)
    }
}
