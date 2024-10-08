package com.jcDevelopment.tictactoeadfree.module.data.gameStatistics

import com.jcDevelopment.tictactoeadfree.module.data.AppDatabase
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameStatisticsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val gameStatisticsModule = module {

    single { get<AppDatabase>().gameStatisticsDao() }

    single {
        GameStatisticsRepository(
            get()
        )
    }

    viewModel { GameStatisticsViewModel(get()) }
}

class GameStatisticsRepository constructor(
    private val gameStatisticsDao: GameStatisticsDao
) {

    fun createNewGameStatistic(gameStatistics: GameStatistics) {
        gameStatisticsDao.insertAll(gameStatistics)
    }

    fun removeGameStatistic(gameStatistics: GameStatistics) {
        gameStatisticsDao.delete(gameStatistics)
    }

    fun getGameStatistics() = gameStatisticsDao.getAll()
}