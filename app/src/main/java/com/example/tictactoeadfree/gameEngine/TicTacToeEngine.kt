package com.example.tictactoeadfree.gameEngine

class TicTacToeEngine constructor(
    private val grid: Int = 3,
    private val winCondition: Int = 3,
    private val is3DBoard: Boolean = false
) {
    private var playGround = mutableListOf<MutableList<MutableList<Int?>>>()

    private var currentPlayer = 0

    private val rowAmountToWin = if (grid == 3) { 3 } else { 4 }

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

        if (currentPlayer == playerCount - 1) {
            currentPlayer = 0
        } else {
            currentPlayer++
        }

        checkForWinCondition()
    }

    private fun checkForWinCondition() {
        //TODO("Not yet implemented")
    }

    interface EndedGameListener {
        fun onPlayerWin()
        fun onDraw()
    }
}