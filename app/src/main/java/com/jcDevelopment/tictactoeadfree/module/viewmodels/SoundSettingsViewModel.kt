package com.jcDevelopment.tictactoeadfree.module.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcDevelopment.tictactoeadfree.module.data.soundSettings.SoundSettings
import com.jcDevelopment.tictactoeadfree.module.data.soundSettings.SoundSettingsRepository
import kotlinx.coroutines.launch

class SoundSettingsViewModel(
    private val soundSettingsRepository: SoundSettingsRepository
) : ViewModel() {
    fun getSoundSettings(): List<SoundSettings> {
        return soundSettingsRepository.getSoundSettings()
    }

    fun updateSoundSettings(soundSettings: SoundSettings) {
        viewModelScope.launch {
            soundSettingsRepository.updateSoundSettings(soundSettings)
        }
    }
}
