package com.example.tictactoeadfree.module.data.gameSettings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gameSettings")
data class GameSettings (
    var isSecondPlayerAi: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)