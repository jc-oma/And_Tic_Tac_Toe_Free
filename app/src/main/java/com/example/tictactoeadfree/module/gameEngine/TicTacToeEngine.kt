package com.example.tictactoeadfree.module.gameEngine

import android.content.Context
import androidx.room.Room
import com.example.tictactoeadfree.module.data.AppDatabase
import com.example.tictactoeadfree.module.data.GameStatistics
import com.example.tictactoeadfree.module.data.GameStatisticsRepository
import com.example.tictactoeadfree.module.viewmodels.GameStatisticsViewModel

class TicTacToeEngine internal constructor(
    private val grid: Int = 3,
    private val is3DBoard: Boolean = false,
    private val context: Context,
    listener: GameListener
) {

    private lateinit var viewModel: GameStatisticsViewModel

    private val dbName = "TIC_TAC_TOE_GAME_STATISTICS_DB"

    //Fixme not on main thread
    private val appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, dbName).allowMainThreadQueries().build()

    private val gameRepo = GameStatisticsRepository.getInstance(appDatabase.gameStatisticsDao())

    private var gameListener: GameListener = listener

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
        gameListener.onInitializeBoard()
        playGround = mutableList()
    }

    fun getCurrentPlayer(): Int {
        return currentPlayer
    }

    fun playerTurn(positionX: Int, positionY: Int, positionZ: Int = 0) {
        if (!is3DBoard && positionZ > 0) {
            throw IllegalArgumentException("postionZ couldn't be calculated in 2D Game")
        }

        if (currentPlayer == 0) {
            currentPlayer = 1
        }

        gameListener.onSwitchPlayer(playerNumber = currentPlayer)

        playGround[positionX][positionY][positionZ] = currentPlayer

        checkForWinCondition(positionX, positionY, positionZ)

        if (currentPlayer == playerCount) {
            currentPlayer = 1
        } else {
            currentPlayer++
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
        viewModel = GameStatisticsViewModel(gameRepo)
        viewModel.addGameToStatistic(GameStatistics(wonPlayer = currentPlayer))
        val gameStatistics: List<GameStatistics> = viewModel.getGameStatisticsList()
        gameListener.onGameEnd(currentPlayer)
    }

    interface GameListener {
        fun onGameEnd(wonPlayer: Int)
        fun onSwitchPlayer(playerNumber: Int)
        fun onInitializeBoard()
    }
}