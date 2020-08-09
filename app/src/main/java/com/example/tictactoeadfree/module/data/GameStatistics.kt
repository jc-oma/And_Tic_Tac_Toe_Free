package com.example.tictactoeadfree.module.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "gameStatistics")
data class GameStatistics (
    @PrimaryKey(autoGenerate = true)
    var wonPlayer: Int,
    var neededTurns: Int? = null,
    var timeTakenToWin: Long? = null
)