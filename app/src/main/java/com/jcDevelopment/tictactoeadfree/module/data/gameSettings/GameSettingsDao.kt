package com.jcDevelopment.tictactoeadfree.module.data.gameSettings

import androidx.room.*

@Dao
interface GameSettingsDao {
    @Query("SELECT * FROM gameSettings")
    fun getAll(): List<GameSettings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(gameSettings: GameSettings)

    @Delete
    fun delete(vararg gameSettings: GameSettings)
}