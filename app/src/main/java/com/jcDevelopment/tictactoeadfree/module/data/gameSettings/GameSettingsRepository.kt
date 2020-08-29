package com.jcDevelopment.tictactoeadfree.module.data.gameSettings

import com.jcDevelopment.tictactoeadfree.module.data.AppDatabase
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameSettingsViewModel
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

    fun removeGameSettings(gameSettings: GameSettings) {
        gameSettingsDao.delete(gameSettings)
    }

    fun updateGameSettings(gameSettings: GameSettings){
        gameSettingsDao.insertAll(gameSettings)
    }

    fun getGameStettings() : List<GameSettings> {
        val settingList = gameSettingsDao.getAll()
        return if (settingList.isEmpty()) {
            createNewGameSettings(GameSettings())
            listOf(GameSettings())
        } else settingList
    }

    private fun createNewGameSettings(gameSettings: GameSettings) {
        gameSettingsDao.insertAll(gameSettings)
    }
}