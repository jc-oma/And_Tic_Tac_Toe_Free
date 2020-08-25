package com.jcDevelopment.tictactoeadfree.module.gameEngine

import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameMode
import com.jcDevelopment.tictactoeadfree.module.data.gameStatistics.GameStatistics
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameStatisticsViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

class FourInARowEngine internal constructor(
    listener: GameListener
) : KoinComponent {

    private var currentPlayer = 1

    private val gameStatisticsViewModel by inject<GameStatisticsViewModel>()

    private val gameSettingsViewModel by inject<GameSettingsViewModel>()

    private var gameListener: GameListener = listener

    private var playGround: MutableList<MutableList<Int>> = mutableList()

    private var turns = 0

    private var isGameAgainstAi = false

    private val rowAmountToWin = 4

    private val playerCount = 2

    private val gridX = 7

    private val gridY = 6

    fun initializeBoard() {
        currentPlayer = 1
        gameListener.onInitializeBoard()
        playGround = mutableList()
        turns = 0
        isGameAgainstAi = gameSettingsViewModel.getGameSettings().last().isSecondPlayerAi
    }

    fun gameTurn(positionX: Int) {
        // switch from last draw to a valid player
        resetCurrentPlayerFromDraw()

        checkForIllegalStates(positionX)

        turns++

        gameListener.onSwitchPlayer(playerNumber = currentPlayer)

        val positionY = getNextFreeYPosition(positionX)

        if (positionY != null) {
            playGround[positionX][positionY] = currentPlayer

            val gameOver = checkForWinCondition(positionX, positionY)

            gameListener.onPlayerTurned(positionX, positionY, currentPlayer)

            switchPlayer()

            if (currentPlayer == 2 && isGameAgainstAi && !gameOver) {
                aiTurnProcess()
            }
        }
    }

    fun getNextFreeYPosition(positionX: Int): Int? {
        for ((index, cell) in playGround[positionX].withIndex()) {
            if (cell != 0 && index == 0) {
                return null
            }
            if (cell != 0) {
                return index - 1
            }
            if (index == gridY - 1) {
                return index
            }
        }
        throw IllegalStateException("(O.o) index out of grid bounds")
    }

    private fun aiTurnProcess() {
        gameListener.onAiIsTurning()
        var aiTurnX: Int? = null

        //TODO i bit more than random turns
        while (isPositionDataNullOrOnATakenPosition(aiTurnX)) {
            aiTurnX = (Math.random() * gridX).toInt()
        }
        if (aiTurnX != null) {
            android.os.Handler().postDelayed({
                gameListener.onPlayerTurned(aiTurnX, getNextFreeYPosition(aiTurnX)!!, currentPlayer)
                gameTurn(aiTurnX)
            }, 1000)
        }
    }

    private fun checkForIllegalStates(
        positionX: Int
    ) {
        val nextFreeYPosition = getNextFreeYPosition(positionX)
            ?: throw java.lang.IllegalStateException("Y got out of Bounds")
        if (playGround[positionX][nextFreeYPosition] != 0) {
            throw IllegalStateException("this position was already taken")
        }
    }

    private fun resetCurrentPlayerFromDraw() {
        if (currentPlayer == 0) {
            currentPlayer = 1
        }
    }

    private fun switchPlayer() {
        currentPlayer = (currentPlayer % playerCount) + 1
    }

    private fun isPositionDataNullOrOnATakenPosition(
        aiTurnX: Int?
    ): Boolean {
        if (aiTurnX == null) {
            return true
        }
        val nextFreeYPosition = getNextFreeYPosition(aiTurnX)
        return if (nextFreeYPosition == null) {
            true
        } else {
            playGround[aiTurnX][nextFreeYPosition] != 0
        }
    }

    private fun mutableList() = MutableList(gridX) { MutableList(gridY) { 0 } }

    private fun checkForWinCondition(positionX: Int, positionY: Int): Boolean {
        var playStoneCounterInXAxisStraight = 0
        var playStoneCounterInYAxisStraight = 0
        var playStoneCounterIsDiagonal = 0
        val wonPositions: MutableList<Pair<Int, Int>> = mutableListOf()
        var emptyCells = 0

        //X Straight
        for (x in 0 until gridX) {
            if (playGround[x][positionY] == currentPlayer) {
                playStoneCounterInXAxisStraight++
                wonPositions.add(Pair(x, positionY))
            }
            if (playGround[x][positionY] != currentPlayer) {
                playStoneCounterInXAxisStraight = 0
                wonPositions.clear()
            }
            if (rowAmountToWin == playStoneCounterInXAxisStraight) {
                doOnGameEnd(wonPositions)
                return true
            }
        }

        //Y Straight
        for (y in 0 until gridY) {
            if (playGround[positionX][y] == currentPlayer) {
                playStoneCounterInYAxisStraight++
                wonPositions.add(Pair(positionX, y))
            }
            if (playGround[positionX][y] != currentPlayer) {
                playStoneCounterInYAxisStraight = 0
                wonPositions.clear()
            }
            if (rowAmountToWin == playStoneCounterInYAxisStraight) {
                doOnGameEnd(wonPositions)
                return true
            }
        }

        //XY diagonal
        var x = positionX
        var y = positionY
        while (x >= 0 && y >= 0) {
            if (playGround[x][y] == currentPlayer) {
                playStoneCounterIsDiagonal++
                wonPositions.add(Pair(x, y))
            }
            if (playGround[x][y] != currentPlayer) {
                break
            }
            x--
            y--
        }
        x = positionX + 1
        y = positionY + 1
        while (x < gridX && y < gridY) {
            if (playGround[x][y] == currentPlayer) {
                playStoneCounterIsDiagonal++
                wonPositions.add(Pair(x, y))
            }
            if (playGround[x][y] != currentPlayer) {
                break
            }
            x++
            y++
        }
        if (rowAmountToWin == playStoneCounterIsDiagonal) {
            doOnGameEnd(wonPositions)
            return true
        }

        wonPositions.clear()
        playStoneCounterIsDiagonal = 0

        x = positionX
        y = positionY
        while (x >= 0 && y < gridY) {
            if (playGround[x][y] == currentPlayer) {
                playStoneCounterIsDiagonal++
                wonPositions.add(Pair(x, y))
            }
            if (playGround[x][y] != currentPlayer) {
                break
            }
            x--
            y++
        }
        x = positionX + 1
        y = positionY - 1
        while (x < gridX && y >= 0 && y < gridY) {
            if (playGround[x][y] == currentPlayer) {
                playStoneCounterIsDiagonal++
                wonPositions.add(Pair(x, y))
            }
            if (playGround[x][y] != currentPlayer) {
                break
            }
            x++
            y--
        }
        if (rowAmountToWin == playStoneCounterIsDiagonal) {
            doOnGameEnd(wonPositions)
            return true
        }

        // XY diagonal end

        playStoneCounterIsDiagonal = 0
        wonPositions.clear()

        if (rowAmountToWin == playStoneCounterIsDiagonal) {
            doOnGameEnd(wonPositions)
            return true
        }

        wonPositions.clear()

        for (column in playGround) {
            for (cell in column) {
                if (cell == 0) {
                    emptyCells++
                }
            }
        }

        //check for Draw
        if (emptyCells - 1 == 0) {
            currentPlayer = 0
            doOnGameEnd(wonPositions)
            return true
        }
        return false
    }

    private fun doOnGameEnd(wonPosition: MutableList<Pair<Int, Int>>?) {
        gameStatisticsViewModel.addGameToStatistic(
            GameStatistics(
                wonPlayer = currentPlayer,
                neededTurns = turns,
                wasThreeDimensional = false,
                gameMode = GameMode.FOUR_IN_A_ROW.toString()
            )
        )
        gameListener.onGameEnd(currentPlayer, wonPosition)
    }

    interface GameListener {
        fun onGameEnd(
            wonPlayer: Int,
            wonPosition: MutableList<Pair<Int, Int>>?
        )

        fun onSwitchPlayer(playerNumber: Int)
        fun onInitializeBoard()
        fun onAiIsTurning()
        fun onPlayerTurned(
            positionX: Int,
            positionY: Int,
            currentPlayer: Int
        )
    }
}