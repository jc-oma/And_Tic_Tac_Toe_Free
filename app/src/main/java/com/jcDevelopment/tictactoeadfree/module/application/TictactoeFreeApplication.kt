package com.jcDevelopment.tictactoeadfree.module.application

import android.app.Application
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.gameSettingsModule
import com.jcDevelopment.tictactoeadfree.module.data.gameStatistics.gameStatisticsModule
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.multiplayerSettingsModule
import com.jcDevelopment.tictactoeadfree.module.data.roomDatabaseModule
import com.jcDevelopment.tictactoeadfree.module.data.soundSettings.soundSettingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TictactoeFreeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TictactoeFreeApplication)
            modules(listOf(roomDatabaseModule, gameStatisticsModule, gameSettingsModule, multiplayerSettingsModule, soundSettingsModule))
        }
    }
}