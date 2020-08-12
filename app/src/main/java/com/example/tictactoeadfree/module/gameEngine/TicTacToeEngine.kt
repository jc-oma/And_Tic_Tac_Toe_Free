package com.example.tictactoeadfree.module.gameEngine

import com.example.tictactoeadfree.module.data.gameStatistics.GameStatistics
import com.example.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import com.example.tictactoeadfree.module.viewmodels.GameStatisticsViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

class TicTacToeEngine internal constructor(
    private val grid: Int = 3,
    private val is3DBoard: Boolean = false,
    listener: GameListener
) : KoinComponent {

    private val gameStatisticsViewModel by inject<GameStatisticsViewModel>()

    private val gameSettingsViewModel by inject<GameSettingsViewModel>()

    private var gameListener: GameListener = listener

    private var playGround: MutableList<MutableList<MutableList<Int>>> = mutableList()

    private var currentPlayer = 1

    private var turns = 0

    private var isGameAgainstAi = false

    private val rowAmountToWin = if (grid == 3) {
        3
    } else {
        4
    }

    private val playerCount = 2

    fun initializeBoard() {
        currentPlayer = 1
        gameListener.onInitializeBoard()
        playGround = mutableList()
        turns = 0
        isGameAgainstAi = gameSettingsViewModel.getGameSettings().last().isSecondPlayerAi
    }

    fun getCurrentPlayer(): Int {
        return currentPlayer
    }

    fun getCurrentPlayGround(): MutableList<MutableList<MutableList<Int>>> {
        return playGround
    }

    fun gameTurn(positionX: Int, positionY: Int, positionZ: Int = 0) {
        // switch from last draw to a valid player
        if (currentPlayer == 0) {
            currentPlayer = 1
        }

        if (!is3DBoard && positionZ > 0) {
            throw IllegalArgumentException("postionZ couldn't be calculated in 2D Game")
        }

        if (playGround[positionX][positionY][positionZ] != 0) {
            throw IllegalStateException("this position was already taken")
        }

        turns++

        gameListener.onSwitchPlayer(playerNumber = currentPlayer)

        playGround[positionX][positionY][positionZ] = currentPlayer

        checkForWinCondition(positionX, positionY, positionZ)

        switchPlayer()

        if (currentPlayer == 2 && isGameAgainstAi) {
            aiTurnProcess()
        }
    }

    private fun aiTurnProcess() {
        gameListener.onAiIsTurning()
        var aiTurnX: Int? = null
        var aiTurnY: Int? = null
        var aiTurnZ: Int? = null

        while (isPositionDataNull(aiTurnX, aiTurnY, aiTurnZ)) {
            aiTurnX = (Math.random() * grid).toInt()
            aiTurnY = (Math.random() * grid).toInt()
            aiTurnZ = (Math.random() * grid).toInt()
        }

        if (is3DBoard) {
            if (aiTurnX != null && aiTurnY != null && aiTurnZ != null) {
                gameListener.onAiTurned(aiTurnX, aiTurnY, aiTurnZ)
            }
        } else {
            if (aiTurnX != null && aiTurnY != null) {
                gameListener.onAiTurned(aiTurnX, aiTurnY, 0)
            }
        }
    }

    private fun switchPlayer() {
        currentPlayer = (currentPlayer % playerCount) + 1
    }

    private fun isPositionDataNull(aiTurnX: Int?, aiTurnY: Int?, aiTurnZ: Int?): Boolean {
        return if (aiTurnX == null || aiTurnY == null || aiTurnZ == null) {
            true
        } else
            if (is3DBoard) {
                playGround[aiTurnX][aiTurnY][aiTurnZ] != 0
            } else {
                playGround[aiTurnX][aiTurnY][0] != 0
            }
    }

    private fun mutableList() = MutableList(grid) { MutableList(grid) { MutableList(grid) { 0 } } }

    private fun checkForWinCondition(positionX: Int, positionY: Int, positionZ: Int) {
        var playStoneCounterInXAxisStraight = 0
        var playStoneCounterInYAxisStraight = 0
        var playStoneCounterInZAxisStraight = 0
        var playStoneCounterIsDiagonal = 0
        var emptyCells = 0

        //X Straight
        for (x in 0 until grid) {
            if (playGround[x][positionY][positionZ] == currentPlayer) {
                playStoneCounterInXAxisStraight++
            }
            if (playGround[x][positionY][positionZ] != currentPlayer) {
                playStoneCounterInXAxisStraight = 0
            }
            if (rowAmountToWin == playStoneCounterInXAxisStraight) {
                doOnGameEnd()
                return
            }
        }

        //Y Straight
        for (y in 0 until grid) {
            if (playGround[positionX][y][positionZ] == currentPlayer) {
                playStoneCounterInYAxisStraight++
            }
            if (playGround[positionX][y][positionZ] != currentPlayer) {
                playStoneCounterInYAxisStraight = 0
            }
            if (rowAmountToWin == playStoneCounterInYAxisStraight) {
                doOnGameEnd()
                return
            }
        }

        //Z Straight
        for (z in 0 until grid) {
            if (playGround[positionX][positionY][z] == currentPlayer) {
                playStoneCounterInZAxisStraight++
            }
            if (playGround[positionX][positionY][z] != currentPlayer) {
                playStoneCounterInZAxisStraight = 0
            }
            if (rowAmountToWin == playStoneCounterInZAxisStraight) {
                doOnGameEnd()
                return
            }
        }

        //XY diagonal
        var x = positionX
        var y = positionY
        while (x >= 0 && y >= 0) {
            if (playGround[x][y][positionZ] == currentPlayer) {
                playStoneCounterIsDiagonal++
            }
            if (playGround[x][y][positionZ] != currentPlayer) {
                break
            }
            x--
            y--
        }
        x = positionX + 1
        y = positionY + 1
        while (x < grid && y < grid) {
            if (playGround[x][y][positionZ] == currentPlayer) {
                playStoneCounterIsDiagonal++
            }
            if (playGround[x][y][positionZ] != currentPlayer) {
                break
            }
            x++
            y++
        }
        if (rowAmountToWin == playStoneCounterIsDiagonal) {
            doOnGameEnd()
            return
        }

        playStoneCounterIsDiagonal = 0

        x = positionX
        y = positionY
        while (x >= 0 && y < grid) {
            if (playGround[x][y][positionZ] == currentPlayer) {
                playStoneCounterIsDiagonal++
            }
            if (playGround[x][y][positionZ] != currentPlayer) {
                break
            }
            x--
            y++
        }
        x = positionX + 1
        y = positionY - 1
        while (x < grid && y >= 0 && y < grid) {
            if (playGround[x][y][positionZ] == currentPlayer) {
                playStoneCounterIsDiagonal++
            }
            if (playGround[x][y][positionZ] != currentPlayer) {
                break
            }
            x++
            y--
        }
        if (rowAmountToWin == playStoneCounterIsDiagonal) {
            doOnGameEnd()
            return
        }

        // XY diagonal end

        playStoneCounterIsDiagonal = 0

        // XZ diagonal
        x = positionX
        var z = positionZ
        while (x >= 0 && z >= 0) {
            if (playGround[x][positionY][z] == currentPlayer) {
                playStoneCounterIsDiagonal++
            }
            if (playGround[x][positionY][z] != currentPlayer) {
                break
            }
            x--
            z--
        }
        x = positionX + 1
        z = positionZ + 1
        while (x < grid && z < grid) {
            if (playGround[x][positionY][z] == currentPlayer) {
                playStoneCounterIsDiagonal++
            }
            if (playGround[x][positionY][z] != currentPlayer) {
                break
            }
            x++
            z++
        }
        if (rowAmountToWin == playStoneCounterIsDiagonal) {
            doOnGameEnd()
            return
        }

        playStoneCounterIsDiagonal = 0

        x = positionX
        z = positionZ
        while (x >= 0 && z < grid) {
            if (playGround[x][positionY][z] == currentPlayer) {
                playStoneCounterIsDiagonal++
            }
            if (playGround[x][positionY][z] != currentPlayer) {
                break
            }
            x--
            z++
        }
        x = positionX + 1
        z = positionZ + 1
        while (x < grid && z < grid && z >= 0) {
            if (playGround[x][positionY][z] == currentPlayer) {
                playStoneCounterIsDiagonal++
            }
            if (playGround[x][positionY][z] != currentPlayer) {
                break
            }
            x++
            z--
        }
        if (rowAmountToWin == playStoneCounterIsDiagonal) {
            doOnGameEnd()
            return
        }
        // XZ end
        // TODO XZY diagonal
        // Diagonal end

        for (squareCell in playGround) {
            for (linearCell in squareCell) {
                for (cell in linearCell) {
                    if (cell == 0) {
                        emptyCells++
                    }
                }
            }
        }

        //check for Draw
        if ((emptyCells == 0 && is3DBoard) || (emptyCells == grid * grid * (grid - 1) && !is3DBoard)) {
            currentPlayer = 0
            doOnGameEnd()
        }
    }

    private fun doOnGameEnd() {
        gameStatisticsViewModel.addGameToStatistic(
            GameStatistics(
                wonPlayer = currentPlayer,
                neededTurns = turns,
                wasThreeDimensional = is3DBoard
            )
        )
        gameListener.onGameEnd(currentPlayer)
    }

    interface GameListener {
        fun onGameEnd(wonPlayer: Int)
        fun onSwitchPlayer(playerNumber: Int)
        fun onInitializeBoard()
        fun onAiIsTurning()
        fun onAiTurned(positionX: Int, positionY: Int, positionZ: Int)
    }
}