package com.example.tictactoeadfree.module.viewmodels

import androidx.lifecycle.ViewModel
import com.example.tictactoeadfree.module.data.GameStatistics
import com.example.tictactoeadfree.module.data.GameStatisticsRepository
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
