package com.jcDevelopment.tictactoeadfree.module.gameDificulty

import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameDifficulty

object GameOpponentUtils {
    private val aiOpponentList = listOf(
        R.drawable.ic_spooky_thinking_witch,
        R.drawable.ic_spooky_frankenstein,
        R.drawable.ic_spooky_spider
    )

    fun getAiOpponentList(difficulty: GameDifficulty): Int {
        return when(difficulty) {
            GameDifficulty.EASY -> aiOpponentList[2]
            GameDifficulty.MEDIUM -> aiOpponentList[1]
            GameDifficulty.HARD -> aiOpponentList[0]
        }
    }
}