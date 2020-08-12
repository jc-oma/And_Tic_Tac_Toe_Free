package com.example.tictactoeadfree.module.data.gameStatistics

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameStatisticsDao {
    @Query("SELECT * FROM gameStatistics")
    fun getAll(): List<GameStatistics>

    @Insert
    fun insertAll(vararg arrayOfGameStatisticss: GameStatistics)

    @Delete
    fun delete(gameStatistics: GameStatistics)
}