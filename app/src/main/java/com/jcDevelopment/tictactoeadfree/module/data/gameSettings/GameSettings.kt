package com.jcDevelopment.tictactoeadfree.module.data.gameSettings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gameSettings")
data class GameSettings (
    var isSecondPlayerAi: Boolean = false,
    var gameMode: String = GameMode.TIC_TAC_TOE.toString(),
    @PrimaryKey
    var id: Int = 0,
    var difficulty: String = GameDifficulty.EASY.toString()
)

enum class GameMode {
    TIC_TAC_TOE,
    FOUR_IN_A_ROW
}

enum class GameDifficulty{
    EASY,
    MEDIUM,
    HARD
}