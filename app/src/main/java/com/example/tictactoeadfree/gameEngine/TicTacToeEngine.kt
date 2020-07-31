package com.example.tictactoeadfree.gameEngine

class TicTacToeEngine internal constructor(
    private val grid: Int = 3,
    private val is3DBoard: Boolean = false,
    listener: EndedGameListener
) {
    var endedGameListener: EndedGameListener = listener

    private var playGround: MutableList<MutableList<MutableList<Int>>> = mutableList()

    private var currentPlayer = 1

    private val rowAmountToWin = if (grid == 3) {
        3
    } else {
        4
    }

    //TODO in späteren Updates eventuell den Nutzer entscheiden lassen wieviele Spieler es sein sollen
    private val playerCount = 2

    fun initializeBoard() {
        endedGameListener.onInitializeBoard()
        playGround = mutableList()
    }

    fun getCurrentPlayer(): Int {
        return currentPlayer
    }

    fun playerTurn(positionX: Int, positionY: Int, positionZ: Int = 0) {
        if (!is3DBoard && positionZ > 0) {
            throw IllegalArgumentException("postionZ couldn't be calculated in 2D Game")
        }
        playGround[positionX][positionY][positionZ] = currentPlayer

        if (currentPlayer == playerCount) {
            currentPlayer = 1
        } else {
            currentPlayer++
        }

        endedGameListener.onSwitchPlayer(playerNumber = currentPlayer)

        checkForWinCondition()
    }

    private fun mutableList() = MutableList(grid) { MutableList(grid) { MutableList(grid) { 0 } } }

    private fun checkForWinCondition() {
        var currentObservedPlayer = 0
        var playStoneCounterInXAxisStraight = 0
        var playStoneCounterInYAxisStraight = 0
        var index = 0
        var emptyCells = 0

        //Y Axis Straight check
        for (squareCell in playGround) {
            for (linearCell in squareCell) {
                for (cell in linearCell) {
                    //X Axis Straight
                    if (index % grid == 0) {
                        playStoneCounterInXAxisStraight = 0
                        currentObservedPlayer = cell
                    }
                    if (cell != currentObservedPlayer) {
                        playStoneCounterInXAxisStraight = 0
                        currentObservedPlayer = 0
                    }
                    if (cell == currentObservedPlayer && currentObservedPlayer != 0) {
                        playStoneCounterInXAxisStraight++
                    }
                    if (playStoneCounterInXAxisStraight == rowAmountToWin) {
                        endedGameListener.onPlayerWin()
                    }
                    if (cell == 0) {
                        emptyCells++
                    }

                    index++
                }
            }
        }

        //TODO Z Axis

        //check for Draw
        if ((emptyCells == 0 && is3DBoard) || (emptyCells == grid * grid * (grid - 1) && !is3DBoard)) {
            endedGameListener.onDraw()
        }
    }

    interface EndedGameListener {
        fun onPlayerWin()
        fun onDraw()
        fun onSwitchPlayer(playerNumber: Int)
        fun onInitializeBoard()
    }
}