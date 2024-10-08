package com.jcDevelopment.tictactoeadfree.module.gameEngine.tictactoe

import android.os.Handler
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.jcDevelopment.tictactoeadfree.module.blueToothService.BlueToothService
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameDifficulty
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameMode
import com.jcDevelopment.tictactoeadfree.module.data.gameStatistics.GameStatistics
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerDataPackage.MultiplayerDataPackage
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerMode
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameStatisticsViewModel
import com.jcDevelopment.tictactoeadfree.module.viewmodels.MultiplayerSettingsViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.StringReader

class TicTacToeEngine internal constructor(
    private val grid: Int = 3,
    private val is3DBoard: Boolean = false,
    listener: GameListener
) : KoinComponent {

    private val gameStatisticsViewModel by inject<GameStatisticsViewModel>()

    private val gameSettingsViewModel by inject<GameSettingsViewModel>()

    private val multiplayerSettingsViewModel by inject<MultiplayerSettingsViewModel>()

    private var gameListener: GameListener = listener

    private var playGround: MutableList<MutableList<MutableList<Int>>> = mutableList()

    private var currentPlayer = 1

    private var turns = 0

    private var isGameAgainstAi = false

    private val isBluetoothGame = multiplayerSettingsViewModel.getMultiplayerSettings()
        .last().multiplayerMode == MultiplayerMode.BLUETOOTH.toString()

    private val rowAmountToWin = if (grid == 3) {
        3
    } else {
        4
    }

    private val gson = Gson()

    private val playerCount = 2

    fun initializeBoard() {
        currentPlayer = 1
        gameListener.onInitializeBoard()
        playGround = mutableList()
        turns = 0
        isGameAgainstAi = gameSettingsViewModel.getGameSettings().last().isSecondPlayerAi
    }

    fun gameTurn(positionX: Int, positionY: Int, positionZ: Int = 0, isRemoteTurn: Boolean) {
        // switch from last draw to a valid player
        resetCurrentPlayerFromDraw()

        checkForIllegalStates(positionZ, positionX, positionY)

        turns++

        gameListener.onSwitchPlayer(playerNumber = currentPlayer)

        playGround[positionX][positionY][positionZ] = currentPlayer

        val gameOver = checkForWinCondition(positionX, positionY, positionZ)

        gameListener.onPlayerTurned(positionX, positionY, positionZ, currentPlayer)

        switchPlayer()

        if (currentPlayer == 2 && isGameAgainstAi && !gameOver) {
            aiTurnProcess()
        }

        if (!isRemoteTurn && isBluetoothGame
        ) {
            BlueToothService.write(
                gson.toJson(
                    MultiplayerDataPackage(
                        x = positionX,
                        y = positionY,
                        z = positionZ
                    )
                ).toByteArray()
            )
            gameListener.onOpponentIsTurning()
        }
    }

    private fun aiTurnProcess() {
        gameListener.onOpponentIsTurning()
        var aiTurnX: Int? = null
        var aiTurnY: Int? = null
        var aiTurnZ: Int? = null

        val currentGameSetting = gameSettingsViewModel.getGameSettings().first()

        //TODO move to ai object
        while (isPositionDataNullOrOnATakenPosition(
                aiTurnX,
                aiTurnY,
                aiTurnZ
            ) && currentGameSetting.difficulty != GameDifficulty.HARD.toString()
        ) {
            aiTurnX = (Math.random() * grid).toInt()
            aiTurnY = (Math.random() * grid).toInt()
            aiTurnZ = (Math.random() * grid).toInt()
        }

        if (currentGameSetting.difficulty != GameDifficulty.EASY.toString()
        ) {
            val aiMove = TicTacToeAI.findBestMove(playGround)

            if (currentGameSetting.difficulty == GameDifficulty.HARD.toString()
            ) {
                aiTurnX = aiMove.row
                aiTurnY = aiMove.col
            } else {
                val random = Math.random()
                if (currentGameSetting.difficulty == GameDifficulty.MEDIUM.toString() && random > 0.6
                ) {
                    aiTurnX = aiMove.row
                    aiTurnY = aiMove.col
                }
            }
        }

        if (is3DBoard) {
            if (aiTurnX != null && aiTurnY != null && aiTurnZ != null) {
                Handler().postDelayed({
                    gameListener.onPlayerTurned(aiTurnX, aiTurnY, aiTurnZ, currentPlayer)
                    playGround[aiTurnX][aiTurnY][aiTurnZ] = currentPlayer
                    checkForWinCondition(aiTurnX, aiTurnY, aiTurnZ)
                    switchPlayer()
                }, 1000)
            }
        } else {
            if (aiTurnX != null && aiTurnY != null) {
                Handler().postDelayed({
                    gameListener.onPlayerTurned(aiTurnX, aiTurnY, 0, currentPlayer)
                    playGround[aiTurnX][aiTurnY][0] = currentPlayer
                    checkForWinCondition(aiTurnX, aiTurnY, 0)
                    switchPlayer()
                }, 1000)
            }
        }

    }

    private fun isPositionDataNullOrOnATakenPosition(
        aiTurnX: Int?,
        aiTurnY: Int?,
        aiTurnZ: Int?
    ): Boolean {
        return if (aiTurnX == null || aiTurnY == null || aiTurnZ == null) {
            true
        } else
            if (is3DBoard) {
                playGround[aiTurnX][aiTurnY][aiTurnZ] != 0
            } else {
                playGround[aiTurnX][aiTurnY][0] != 0
            }
    }

    private fun checkForIllegalStates(
        positionZ: Int,
        positionX: Int,
        positionY: Int
    ) {
        if (!is3DBoard && positionZ > 0) {
            throw IllegalArgumentException("postionZ couldn't be calculated in 2D Game")
        }

        if (playGround[positionX][positionY][positionZ] != 0) {
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

    private fun mutableList() = MutableList(grid) { MutableList(grid) { MutableList(grid) { 0 } } }

    private fun checkForWinCondition(positionX: Int, positionY: Int, positionZ: Int): Boolean {
        var playStoneCounterInXAxisStraight = 0
        var playStoneCounterInYAxisStraight = 0
        var playStoneCounterInZAxisStraight = 0
        var playStoneCounterIsDiagonal = 0
        val wonPositions: MutableList<Triple<Int, Int, Int>> = mutableListOf()
        var emptyCells = 0

        //X Straight
        for (x in 0 until grid) {
            if (playGround[x][positionY][positionZ] == currentPlayer) {
                playStoneCounterInXAxisStraight++
                wonPositions.add(Triple(x, positionY, positionZ))
            }
            if (playGround[x][positionY][positionZ] != currentPlayer) {
                playStoneCounterInXAxisStraight = 0
                wonPositions.clear()
            }
            if (rowAmountToWin == playStoneCounterInXAxisStraight) {
                doOnGameEnd(wonPositions)
                return true
            }
        }

        //Y Straight
        for (y in 0 until grid) {
            if (playGround[positionX][y][positionZ] == currentPlayer) {
                playStoneCounterInYAxisStraight++
                wonPositions.add(Triple(positionX, y, positionZ))
            }
            if (playGround[positionX][y][positionZ] != currentPlayer) {
                playStoneCounterInYAxisStraight = 0
                wonPositions.clear()
            }
            if (rowAmountToWin == playStoneCounterInYAxisStraight) {
                doOnGameEnd(wonPositions)
                return true
            }
        }

        //Z Straight
        for (z in 0 until grid) {
            if (playGround[positionX][positionY][z] == currentPlayer) {
                playStoneCounterInZAxisStraight++
                wonPositions.add(Triple(positionX, positionY, positionZ))
            }
            if (playGround[positionX][positionY][z] != currentPlayer) {
                playStoneCounterInZAxisStraight = 0
                wonPositions.clear()
            }
            if (rowAmountToWin == playStoneCounterInZAxisStraight) {
                doOnGameEnd(wonPositions)
                return true
            }
        }

        //XY diagonal
        var x = positionX
        var y = positionY
        while (x >= 0 && y >= 0) {
            if (playGround[x][y][positionZ] == currentPlayer) {
                playStoneCounterIsDiagonal++
                wonPositions.add(Triple(x, y, positionZ))
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
                wonPositions.add(Triple(x, y, positionZ))
            }
            if (playGround[x][y][positionZ] != currentPlayer) {
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
        while (x >= 0 && y < grid) {
            if (playGround[x][y][positionZ] == currentPlayer) {
                playStoneCounterIsDiagonal++
                wonPositions.add(Triple(x, y, positionZ))
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
                wonPositions.add(Triple(x, y, positionZ))
            }
            if (playGround[x][y][positionZ] != currentPlayer) {
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

        // XZ diagonal
        x = positionX
        var z = positionZ
        while (x >= 0 && z >= 0) {
            if (playGround[x][positionY][z] == currentPlayer) {
                playStoneCounterIsDiagonal++
                wonPositions.add(Triple(x, positionY, z))
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
                wonPositions.add(Triple(x, positionY, z))
            }
            if (playGround[x][positionY][z] != currentPlayer) {
                break
            }
            x++
            z++
        }
        if (rowAmountToWin == playStoneCounterIsDiagonal) {
            doOnGameEnd(wonPositions)
            return true
        }

        playStoneCounterIsDiagonal = 0
        wonPositions.clear()

        x = positionX
        z = positionZ
        while (x >= 0 && z < grid) {
            if (playGround[x][positionY][z] == currentPlayer) {
                playStoneCounterIsDiagonal++
                wonPositions.add(Triple(x, positionY, z))
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
                wonPositions.add(Triple(x, positionY, z))
            }
            if (playGround[x][positionY][z] != currentPlayer) {
                break
            }
            x++
            z--
        }
        if (rowAmountToWin == playStoneCounterIsDiagonal) {
            doOnGameEnd(wonPositions)
            return true
        }

        wonPositions.clear()

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
            doOnGameEnd(wonPositions)
            return true
        }
        return false
    }

    private fun doOnGameEnd(wonPosition: MutableList<Triple<Int, Int, Int>>?) {
        gameStatisticsViewModel.addGameToStatistic(
            GameStatistics(
                wonPlayer = currentPlayer,
                neededTurns = turns,
                wasThreeDimensional = is3DBoard,
                gameMode = GameMode.TIC_TAC_TOE.toString()
            )
        )
        gameListener.onGameEnd(currentPlayer, wonPosition)
    }

    fun initMultiplayerListener() {
        BlueToothService.getMessageObservable().doOnNext { multiplayerPackageString ->
            val reader = JsonReader(StringReader(multiplayerPackageString))
            reader.isLenient = true
            val packageData = gson.fromJson<MultiplayerDataPackage>(
                reader,
                MultiplayerDataPackage::class.java
            )

            packageData.leaveGame?.let {
                if (it) {
                    gameListener.onOpponentLeft()
                }
            }

            packageData.x?.let { x ->
                packageData.y?.let { y ->
                    gameTurn(x, y, isRemoteTurn = true)
                }
            }

            packageData.restartGame?.let{
                if (it) {
                    gameListener.onRestartGame()
                }
            }
        }.subscribe()

        BlueToothService.getConnectionObservable().doOnNext {
            if (it != BlueToothService.STATE_CONNECTED) {
                gameListener.onOpponentLeft()
            }
        }.subscribe()
    }

    interface GameListener {
        fun onGameEnd(
            wonPlayer: Int,
            wonPosition: MutableList<Triple<Int, Int, Int>>?
        )

        fun onOpponentLeft()
        fun onSwitchPlayer(playerNumber: Int)
        fun onInitializeBoard()
        fun onOpponentIsTurning()
        fun onPlayerTurned(
            positionX: Int,
            positionY: Int,
            positionZ: Int,
            currentPlayer: Int
        )

        fun onRestartGame()
    }
}