package com.example.tictactoeadfree.module.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "gameStatistics")
data class GameStatistics (
    var wonPlayer: Int,
    var neededTurns: Int? = null,
    var timeTakenToWin: Long? = null,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)