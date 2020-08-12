package com.example.tictactoeadfree.module.data

import androidx.room.Room
import com.example.tictactoeadfree.module.viewmodels.GameStatisticsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val gameStatisticsModule = module {

    single { Room.databaseBuilder(get(), AppDatabase::class.java, "gameStatistics").build() }

    single { get<AppDatabase>().gameStatisticsDao() }

    single { GameStatisticsRepository(get()) }

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

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: GameStatisticsRepository? = null

        fun getInstance(gameStatisticsDao: GameStatisticsDao) =
            instance ?: synchronized(this) {
                instance ?: GameStatisticsRepository(gameStatisticsDao).also { instance = it }
            }
    }
}