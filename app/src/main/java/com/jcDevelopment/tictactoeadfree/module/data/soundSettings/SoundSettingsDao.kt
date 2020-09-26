package com.jcDevelopment.tictactoeadfree.module.data.soundSettings

import androidx.room.*

@Dao
interface SoundSettingsDao {
    @Query("SELECT * FROM soundSettings")
    fun getAll(): List<SoundSettings>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(soundSetting: SoundSettings)

    @Delete
    fun delete(vararg soundSetting: SoundSettings)
}