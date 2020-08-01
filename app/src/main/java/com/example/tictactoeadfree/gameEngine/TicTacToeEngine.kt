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
        var playStoneCounterInXAxisStraight: MutableList<Int> = MutableList(grid) {0}
        var playStoneCounterInYAxisStraight = 0
        var index = 0
        var emptyCells = 0

        for ((squareIndex,squareCell) in playGround.withIndex()) {
            for ((linearIndex, linearCell) in squareCell.withIndex()) {
                for ((cellIndex, cell) in linearCell.withIndex()) {
                    //X Axis Straight check

                    //Y Axis Straight check //TODO modulo Index eventuell entfernen
                    if (index % (grid * grid) == 0) {
                        playStoneCounterInYAxisStraight = 0
                    }

                    if (index % grid == 0) {
                        if (cell != currentObservedPlayer) {
                            currentObservedPlayer = cell
                            playStoneCounterInYAxisStraight = 0
                        }
                        if (cell == currentObservedPlayer && currentObservedPlayer != 0) {
                            playStoneCounterInYAxisStraight++
                        }
                        if (playStoneCounterInYAxisStraight == rowAmountToWin) {
                            endedGameListener.onPlayerWin()
                        }
                    }
                    //Y Axis Straight end

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