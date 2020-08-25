package com.jcDevelopment.tictactoeadfree.module.viewmodels

import androidx.lifecycle.ViewModel
import com.jcDevelopment.tictactoeadfree.module.data.gameStatistics.GameStatistics
import com.jcDevelopment.tictactoeadfree.module.data.gameStatistics.GameStatisticsRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GameStatisticsViewModel(
    private val gameStatisticsRepository: GameStatisticsRepository
) : ViewModel() {
    fun addGameToStatistic(gameStatistics: GameStatistics) {
        viewModelScope.launch {
            gameStatisticsRepository.createNewGameStatistic(gameStatistics)
        }
    }

    fun getGameStatisticsList() : List<GameStatistics>{
        return gameStatisticsRepository.getGameStatistics()
    }
}
