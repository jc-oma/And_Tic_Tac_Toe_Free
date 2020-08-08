package com.example.tictactoeadfree.module.gameEngine

class TicTacToeEngine internal constructor(
    private val grid: Int = 3,
    private val is3DBoard: Boolean = false,
    listener: GameListener
) {
    var gameListener: GameListener = listener

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
                currentPlayerWinsGame()
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
                currentPlayerWinsGame()
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
                currentPlayerWinsGame()
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
            currentPlayerWinsGame()
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
            currentPlayerWinsGame()
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
            currentPlayerWinsGame()
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
            currentPlayerWinsGame()
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
            gameListener.onDraw()
        }
    }

    private fun currentPlayerWinsGame() {
        gameListener.onPlayerWin(currentPlayer)
    }

    interface GameListener {
        fun onPlayerWin(wonPlayer: Int)
        fun onDraw()
        fun onSwitchPlayer(playerNumber: Int)
        fun onInitializeBoard()
    }
}