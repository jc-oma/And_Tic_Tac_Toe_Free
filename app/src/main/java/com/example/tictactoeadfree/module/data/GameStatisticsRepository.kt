package com.example.tictactoeadfree.module.data

class GameStatisticsRepository private constructor(
    private val gameStatisticsDao: GameStatisticsDao
) {

    suspend fun createNewGameStatistic(gameStatistics: GameStatistics) {
        gameStatisticsDao.insertAll(gameStatistics)
    }

    suspend fun removeGameStatistic(gameStatistics: GameStatistics) {
        gameStatisticsDao.delete(gameStatistics)
    }

    suspend fun getGameStatistics() = gameStatisticsDao.getAll()

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