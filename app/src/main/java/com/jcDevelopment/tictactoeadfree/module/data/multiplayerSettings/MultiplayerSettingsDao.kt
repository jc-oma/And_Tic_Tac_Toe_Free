package com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings

import androidx.room.*

@Dao
interface MultiplayerSettingsDao {
    @Query("SELECT * FROM multiplayerSettings")
    fun getAll(): List<MultiplayerSettings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(multiplayerSettings: MultiplayerSettings)

    @Delete
    fun delete(vararg multiplayerSettings: MultiplayerSettings)
}