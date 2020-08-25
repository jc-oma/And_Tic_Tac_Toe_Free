package com.jcDevelopment.tictactoeadfree.module.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettings
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettingsDao
import com.jcDevelopment.tictactoeadfree.module.data.gameStatistics.GameStatistics
import com.jcDevelopment.tictactoeadfree.module.data.gameStatistics.GameStatisticsDao
import org.koin.dsl.module

val roomDatabaseModule = module {

    //Fixme not on main thread
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "gameSettings")
            .allowMainThreadQueries().build()
    }

}

@Database(
    entities = [GameStatistics::class, GameSettings::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameStatisticsDao(): GameStatisticsDao
    abstract fun gameSettingsDao(): GameSettingsDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        // TODO change DB Name
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "GameStatistics.db"
            )
                .build()
    }
}