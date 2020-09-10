package com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings

import com.jcDevelopment.tictactoeadfree.module.data.AppDatabase
import com.jcDevelopment.tictactoeadfree.module.viewmodels.MultiplayerSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val multiplayerSettingsModule = module {

    single { get<AppDatabase>().multiplayerSettingsDao() }

    single {
        MultiplayerSettingsRepository(
            get()
        )
    }

    viewModel { MultiplayerSettingsViewModel(get()) }
}

class MultiplayerSettingsRepository constructor(
    private val multiplayerSettingsDao: MultiplayerSettingsDao
) {

    fun removeMultiplayerSettings(multiplayerSettings: MultiplayerSettings) {
        multiplayerSettingsDao.delete(multiplayerSettings)
    }

    fun updateMultiplayerSettings(multiplayerSettings: MultiplayerSettings) {
        multiplayerSettingsDao.insertAll(multiplayerSettings)
    }

    fun getMultiplayerSettings() : List<MultiplayerSettings> {
        val settingList = multiplayerSettingsDao.getAll()
        return if (settingList.isEmpty()) {
            createNewMultiplayerSettings(MultiplayerSettings())
            listOf(MultiplayerSettings())
        } else settingList
    }

    private fun createNewMultiplayerSettings(multiplayerSettings: MultiplayerSettings) {
        multiplayerSettingsDao.insertAll(multiplayerSettings)
    }
}