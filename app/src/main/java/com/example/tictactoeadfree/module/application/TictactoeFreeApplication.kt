package com.example.tictactoeadfree.module.application

import android.app.Application
import com.example.tictactoeadfree.module.data.gameStatisticsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TictactoeFreeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@TictactoeFreeApplication)
            modules(listOf(gameStatisticsModule))
        }
    }
}