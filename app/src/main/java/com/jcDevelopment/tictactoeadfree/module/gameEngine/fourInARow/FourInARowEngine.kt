package com.jcDevelopment.tictactoeadfree.module.gameEngine.fourInARow

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

class FourInARowEngine internal constructor(
    listener: GameListener
) : KoinComponent {

    private var currentPlayer = 1

    private val gameStatisticsViewModel by inject<GameStatisticsViewModel>()

    private val gameSettingsViewModel by inject<GameSettingsViewModel>()

    private val multiplayerSettingsViewModel by inject<MultiplayerSettingsViewModel>()

    private var gameListener: GameListener = listener

    private var playGround: MutableList<MutableList<Int>> = mutableList()

    private var turns = 0

    private var isGameAgainstAi = false

    private val rowAmountToWin = 4

    private val playerCount = 2

    private val gridX = 7

    private val gridY = 6

    private var nextAiTurnX: Pair<Int?, Int?>? = null

    private val ai = FourInARowAi

    private val gson = Gson()

    private val isBluetoothGame = multiplayerSettingsViewModel.getMultiplayerSettings()
        .last().multiplayerMode == MultiplayerMode.BLUETOOTH.toString()

    fun initializeBoard() {
        currentPlayer = 1
        gameListener.onInitializeBoard()
        playGround = mutableList()
        turns = 0
        isGameAgainstAi = gameSettingsViewModel.getGameSettings().last().isSecondPlayerAi
    }

    fun gameTurn(positionX: Int, isRemoteTurn: Boolean) {
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

        if (!isRemoteTurn && isBluetoothGame
        ) {
            BlueToothService.write(
                gson.toJson(
                    MultiplayerDataPackage(
                        x = positionX,
                        y = positionY
                    )
                ).toByteArray()
            )
            gameListener.onOpponentIsTurning()
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
                    gameListener.onOpponentLeftGame()
                }
            }

            packageData.x?.let { x ->
                gameTurn(x, isRemoteTurn = true)
            }

            packageData.restartGame?.let {
                if (it) {
                    gameListener.onRestartGame()
                }
            }
        }.subscribe()

        BlueToothService.getConnectionObservable().doOnNext {
            if (it != BlueToothService.STATE_CONNECTED) {
                gameListener.onOpponentLeftGame()
            }
        }.subscribe()
    }

    private fun aiTurnProcess() {
        gameListener.onOpponentIsTurning()
        var aiTurnX: Int? = null

        if (nextAiTurnX?.first != null) {
            aiTurnX = nextAiTurnX?.first
        } else if (nextAiTurnX?.second != null) {
            aiTurnX = nextAiTurnX?.second
        }

        while (isPositionDataNullOrOnATakenPosition(aiTurnX)) {
            aiTurnX = (Math.random() * gridX).toInt()
        }

        val gameSettings = gameSettingsViewModel.getGameSettings().last()

        if (turns > 4 && gameSettings.difficulty == GameDifficulty.MEDIUM.toString()) {
            aiTurnX = ai.getBestMove(playGround)
        }

        if (aiTurnX != null) {
            android.os.Handler().postDelayed({
                gameTurn(aiTurnX, false)

                nextAiTurnX = null
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
        if (aiTurnX >= gridX || aiTurnX < 0) {
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

            //ai conditions
            if (rowAmountToWin - 1 == playStoneCounterInXAxisStraight) {
                nextAiTurnX = Pair(x + 1, x - (rowAmountToWin - 1))
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

            //ai conditions
            if (rowAmountToWin - 1 == playStoneCounterInYAxisStraight) {
                nextAiTurnX = Pair(positionX, null)
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
        fun onOpponentIsTurning()
        fun onPlayerTurned(
            positionX: Int,
            positionY: Int,
            currentPlayer: Int
        )

        fun onOpponentLeftGame()
        fun onRestartGame()
    }
}