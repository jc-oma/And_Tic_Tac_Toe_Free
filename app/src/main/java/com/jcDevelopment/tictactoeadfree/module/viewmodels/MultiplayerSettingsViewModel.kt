package com.jcDevelopment.tictactoeadfree.module.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerSettings
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerSettingsRepository
import kotlinx.coroutines.launch

class MultiplayerSettingsViewModel(
    private val multiplayerSettingsRepository: MultiplayerSettingsRepository
) : ViewModel() {
    fun getMultiplayerSettings(): List<MultiplayerSettings> {
        return multiplayerSettingsRepository.getMultiplayerSettings()
    }

    fun updateMultiplayersettings(multiplayerSettings: MultiplayerSettings) {
        viewModelScope.launch {
            multiplayerSettingsRepository.updateMultiplayerSettings(multiplayerSettings)
        }
    }
}