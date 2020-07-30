package com.example.tictactoeadfree.gameEngine

class TicTacToeEngine internal constructor(
    private val grid: Int = 3,
    private val is3DBoard: Boolean = false,
    private var listener: EndedGameListener
) {
    var endedGameListener: EndedGameListener = listener

    private var playGround: MutableList<MutableList<MutableList<Int>>> = MutableList(grid) { MutableList(grid) { MutableList(grid) { 0 } } }

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
        MutableList(grid) { MutableList(grid) { MutableList(grid) { 0 } } }
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

    private fun checkForWinCondition() {
        var currentObservedPlayer = 0
        var playStoneCounterInXAxisStraight = 0
        var playStoneCounterInYAxisStraight = 0
        var index = 0
        var emptyCells = 0
        
        //X Axis Straight check
        for (squareCell in playGround) {
            for (linearCell in squareCell) {
                for (cell in linearCell) {
                    //X Axis Straight
                    if (index % grid == 0) {
                        playStoneCounterInXAxisStraight = 0
                        currentObservedPlayer = 0
                    }
                    if (cell != currentObservedPlayer) {
                        playStoneCounterInXAxisStraight = 0
                        currentObservedPlayer = 0
                    }
                    if (cell == currentObservedPlayer) {
                        playStoneCounterInXAxisStraight++
                    }
                    if (playStoneCounterInXAxisStraight == rowAmountToWin){
                        endedGameListener.onPlayerWin()
                    }
                    if (cell == 0) {
                        emptyCells++
                    }

                    index++
                }
            }
        }

        //Y Axis Straight

        //check for Draw
        if ((emptyCells == 0 && is3DBoard) || (emptyCells == 18 && !is3DBoard)) {
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