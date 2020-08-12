package com.example.tictactoeadfree.module.data.gameSettings

import com.example.tictactoeadfree.module.data.AppDatabase
import com.example.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val gameSettingsModule = module {

    single { get<AppDatabase>().gameSettingsDao() }

    single {
        GameSettingsRepository(
            get()
        )
    }

    viewModel { GameSettingsViewModel(get()) }
}

class GameSettingsRepository constructor(
    private val gameSettingsDao: GameSettingsDao
) {

    fun createNewGameSettings(gameSettings: GameSettings) {
        gameSettingsDao.insertAll(gameSettings)
    }

    fun removeGameSettings(gameSettings: GameSettings) {
        gameSettingsDao.delete(gameSettings)
    }

    fun getGameStettings() = gameSettingsDao.getAll()
}