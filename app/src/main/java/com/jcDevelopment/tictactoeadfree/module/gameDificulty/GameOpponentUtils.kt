package com.jcDevelopment.tictactoeadfree.module.gameDificulty

import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameDifficulty
import com.jcDevelopment.tictactoeadfree.module.sounds.SoundPlayer

object GameOpponentUtils {
    private val aiOpponentList = listOf(
        R.drawable.ic_spooky_thinking_witch,
        R.drawable.ic_spooky_frankenstein,
        R.drawable.ic_spooky_spider
    )

    fun getAiOpponentList(difficulty: GameDifficulty): Int {
        return when (difficulty) {
            GameDifficulty.EASY -> aiOpponentList[2]
            GameDifficulty.MEDIUM -> aiOpponentList[1]
            GameDifficulty.HARD -> aiOpponentList[0]
        }
    }

    fun getOpponentSoundId(difficulty: GameDifficulty): SoundPlayer.SoundList {
        return when (difficulty) {
            GameDifficulty.EASY -> SoundPlayer.SoundList.SPIDER_THINKING
            GameDifficulty.MEDIUM -> SoundPlayer.SoundList.FRANKENSTEIN_THINKING
            GameDifficulty.HARD -> SoundPlayer.SoundList.WITCH_THINKING
        }
    }
}