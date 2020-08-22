package com.example.tictactoeadfree.module.data.gameStatistics

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tictactoeadfree.module.data.gameSettings.GameMode


@Entity(tableName = "gameStatistics")
data class GameStatistics(
    var wonPlayer: Int,
    var neededTurns: Int? = null,
    var timeTakenToWin: Long? = null,
    var gridSize: Int? = null,
    var wasThreeDimensional: Boolean = false,
    var gameMode: String,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)