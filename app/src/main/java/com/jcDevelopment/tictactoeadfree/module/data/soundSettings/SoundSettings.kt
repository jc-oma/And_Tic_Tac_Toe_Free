package com.jcDevelopment.tictactoeadfree.module.data.soundSettings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "soundSettings")
data class SoundSettings (
    val isMusicPlaying: Boolean = true,
    @PrimaryKey
    var id: Int = 0
)