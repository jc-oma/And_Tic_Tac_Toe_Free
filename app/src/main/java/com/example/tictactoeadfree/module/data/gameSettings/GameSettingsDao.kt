package com.example.tictactoeadfree.module.data.gameSettings

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameSettingsDao {
    @Query("SELECT * FROM gameSettings")
    fun getAll(): List<GameSettings>

    @Insert
    fun insertAll(vararg arrayOfGameSettings: GameSettings)

    @Delete
    fun delete(gameSettings: GameSettings)
}