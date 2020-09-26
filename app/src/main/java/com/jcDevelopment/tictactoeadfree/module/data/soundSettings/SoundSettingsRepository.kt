package com.jcDevelopment.tictactoeadfree.module.data.soundSettings

import com.jcDevelopment.tictactoeadfree.module.data.AppDatabase
import com.jcDevelopment.tictactoeadfree.module.viewmodels.SoundSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val soundSettingsModule = module {

    single { get<AppDatabase>().soundSettingsDao() }

    single {
        SoundSettingsRepository(
            get()
        )
    }

    viewModel { SoundSettingsViewModel(get()) }
}

class SoundSettingsRepository constructor(
    private val soundSettingsDao: SoundSettingsDao
) {

    fun removeSoundSettings(soundSettings: SoundSettings) {
        soundSettingsDao.delete(soundSettings)
    }

    fun updateSoundSettings(soundSettings: SoundSettings){
        soundSettingsDao.insertAll(soundSettings)
    }

    fun getSoundSettings() : List<SoundSettings> {
        val settingList = soundSettingsDao.getAll()
        return if (settingList.isEmpty()) {
            createNewSoundSettings(SoundSettings())
            listOf(SoundSettings())
        } else settingList
    }

    private fun createNewSoundSettings(soundSettings: SoundSettings) {
        soundSettingsDao.insertAll(soundSettings)
    }
}