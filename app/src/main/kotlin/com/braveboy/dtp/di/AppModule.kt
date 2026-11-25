package com.braveboy.dtp.di

import com.braveboy.dtp.data.remote.datasource.SettingsRemoteDataSource
import com.braveboy.dtp.data.remote.datasource.SettingsRemoteDataSourceImpl
import com.braveboy.dtp.data.repository.SettingsRepositoryImpl
import com.braveboy.dtp.domain.repository.SettingsRepository
import com.braveboy.dtp.domain.usecase.GetRootSettingsUseCase
import com.braveboy.dtp.ui.setting.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Data Sources
    single<SettingsRemoteDataSource> { SettingsRemoteDataSourceImpl(androidContext()) }

    // Repository
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }

    // Use Cases
    factory { GetRootSettingsUseCase(get()) }

    // ViewModel
    viewModel { SettingsViewModel(get()) }
}
