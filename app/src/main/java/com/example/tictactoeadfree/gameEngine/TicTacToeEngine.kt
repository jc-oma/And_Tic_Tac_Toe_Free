package com.example.tictactoeadfree.gameEngine

import java.lang.IllegalArgumentException

class TicTacToeEngine constructor(
    val grid: Int = 3,
    val winCondition: Int = 3,
    private val is3DBoard: Boolean = false
) {
    private var playGround = mutableListOf<MutableList<MutableList<Any?>>>()

    private var currentPlayer = 1

    //TODO in späteren Updates eventuell den Nutzer entscheiden lassen wieviele Spieler es sein sollen
    private val playerCount = 2

    fun getCurrentPlayer(): Int {
        return currentPlayer
    }

    fun playerTurn(positionX: Int, positionY: Int, positionZ: Int = 0) {
        if (!is3DBoard && positionZ > 0) {
            throw IllegalArgumentException("postionZ couldn't be calculated in 2D Game")
        }
        playGround[positionX][positionY][positionZ] = currentPlayer

        checkForWinCondition()
    }

    private fun checkForWinCondition() {
        TODO("Not yet implemented")
    }

    interface EndedGameListener {
        fun onPlayerWin()
        fun onDraw()
    }
}