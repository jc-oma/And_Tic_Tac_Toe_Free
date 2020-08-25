package com.jcDevelopment.tictactoeadfree.module.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettings
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettingsRepository
import kotlinx.coroutines.launch

class GameSettingsViewModel(
    private val gameSettingsRepository: GameSettingsRepository
) : ViewModel() {
    fun createGameSettings(gameSettings: GameSettings) {
        viewModelScope.launch {
            gameSettingsRepository.createNewGameSettings(gameSettings)
        }
    }

    fun getGameSettings(): List<GameSettings> {
        return gameSettingsRepository.getGameStettings()
    }
}