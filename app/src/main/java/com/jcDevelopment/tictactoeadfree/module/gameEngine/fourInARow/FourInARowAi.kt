package com.jcDevelopment.tictactoeadfree.module.gameEngine.fourInARow

import com.jcDevelopment.tictactoeadfree.module.utilityis.deepCopy2

object FourInARowAi {
    private const val rowAmountToWin = 4
    private val currentPlayerList = listOf(1, 2)

    fun getBestMove(playGround: MutableList<MutableList<Int>>): Int {
        //init on left -> index 0
        var bestScore: Int? = null
        var bestScoreTest = 0
        var bestMove = 0
        var isChangedMove = false

        var testBoard: MutableList<MutableList<Int>> = mutableListOf()
        var testBoard2: MutableList<MutableList<Int>> = mutableListOf()
        var testBoard3: MutableList<MutableList<Int>> = mutableListOf()
        var testBoard4: MutableList<MutableList<Int>> = mutableListOf()

        // first turn test by AI
        for (aiXTurn in 0 until 7) {
            if (getNextFreeYPosition(aiXTurn, playGround) == null) {
                break
            }

            testBoard = playGround.deepCopy2 { it }
            bestScoreTest = 0

            val aiYTurn = getNextFreeYPosition(aiXTurn, testBoard)
            if (aiYTurn != null) {
                testBoard[aiXTurn][aiYTurn] = 2
                bestScoreTest += evaluateMove(aiXTurn, aiYTurn, 1, testBoard)

                for (column1 in 0 until 7) {
                    testBoard2 = testBoard.deepCopy2 { it }
                    getNextFreeYPosition(column1, testBoard2)?.let {
                        testBoard2[column1][it] = 1
                        bestScoreTest += evaluateMove(column1, it, 2, testBoard2)
                    }

                    for (column2 in 0 until 7) {
                        testBoard3 = testBoard2.deepCopy2 { it }
                        getNextFreeYPosition(column2, testBoard3)?.let {
                            testBoard3[column2][it] = 2
                            bestScoreTest += evaluateMove(column2, it, 3, testBoard3)
                        }

                        for (column3 in 0 until 7) {
                            testBoard4 = testBoard3.deepCopy2 { it }
                            getNextFreeYPosition(column3, testBoard4)?.let {
                                testBoard4[column3][it] = 1
                                bestScoreTest += evaluateMove(column3, it, 4, testBoard4)
                            }
                        }
                    }
                }
            }


            if (bestScore == null || bestScoreTest > bestScore) {
                isChangedMove = true
                bestScore = bestScoreTest
                bestScoreTest = 0
                bestMove = aiXTurn
            }
        }
        return if (isChangedMove) bestMove else getRandomMove(playGround)
    }

    private fun getRandomMove(playGround: MutableList<MutableList<Int>>): Int {
        val testBoard = playGround.deepCopy2 { it }
        var randomMove: Int? = null
        while (randomMove == null || getNextFreeYPosition(randomMove, testBoard) == null) {
            randomMove = (Math.random() * 7).toInt()
        }
        return randomMove
    }


    private fun getNextFreeYPosition(
        positionX: Int,
        testBoard: MutableList<MutableList<Int>>
    ): Int? {
        for ((index, cell) in testBoard[positionX].withIndex()) {
            if (cell != 0 && index == 0) {
                return null
            }
            if (cell != 0) {
                return index - 1
            }
            if (index == 6 - 1) {
                return index
            }
        }
        throw IllegalStateException("(O.o) index out of grid bounds")
    }

    private fun evaluateMove(
        positionX: Int,
        positionY: Int,
        turnMultiplier: Int,
        testboard: MutableList<MutableList<Int>>
    ): Int {
        var playStoneCounterInXAxisStraight = 0
        var playStoneCounterInYAxisStraight = 0
        var playStoneCounterIsDiagonal = 0
        val wonPositions: MutableList<Pair<Int, Int>> = mutableListOf()
        var emptyCells = 0
        var points = 0

        for (currentPlayer in currentPlayerList) {
            //X Straight
            for (x in 0 until 7) {
                if (testboard[x][positionY] == currentPlayer) {
                    playStoneCounterInXAxisStraight++
                    wonPositions.add(Pair(x, positionY))
                }
                if (testboard[x][positionY] != currentPlayer) {
                    playStoneCounterInXAxisStraight = 0
                    wonPositions.clear()
                }
                if (rowAmountToWin == playStoneCounterInXAxisStraight - 1) {
                    points += getPoints(currentPlayer, turnMultiplier, tripleWarner = true)
                }
                if (rowAmountToWin == playStoneCounterInXAxisStraight) {
                    points += getPoints(currentPlayer, turnMultiplier)
                }
            }

            //Y Straight
            for (y in 0 until 6) {
                if (testboard[positionX][y] == currentPlayer) {
                    playStoneCounterInYAxisStraight++
                    wonPositions.add(Pair(positionX, y))
                }
                if (testboard[positionX][y] != currentPlayer) {
                    playStoneCounterInYAxisStraight = 0
                    wonPositions.clear()
                }
                if (rowAmountToWin == playStoneCounterInYAxisStraight - 1) {
                    points += getPoints(
                        currentPlayer = currentPlayer,
                        turnMultiplier = turnMultiplier, tripleWarner = true
                    )
                }
                if (rowAmountToWin == playStoneCounterInYAxisStraight) {
                    points += getPoints(currentPlayer, turnMultiplier)
                }
            }

            //XY diagonal
            var x = positionX
            var y = positionY
            while (x >= 0 && y >= 0) {
                if (testboard[x][y] == currentPlayer) {
                    playStoneCounterIsDiagonal++
                    wonPositions.add(Pair(x, y))
                }
                if (testboard[x][y] != currentPlayer) {
                    break
                }
                x--
                y--
            }
            x = positionX + 1
            y = positionY + 1
            while (x < 7 && y < 6) {
                if (testboard[x][y] == currentPlayer) {
                    playStoneCounterIsDiagonal++
                    wonPositions.add(Pair(x, y))
                }
                if (testboard[x][y] != currentPlayer) {
                    break
                }
                x++
                y++
            }
            if (rowAmountToWin == playStoneCounterIsDiagonal - 1) {
                points += getPoints(currentPlayer, turnMultiplier, tripleWarner = true)
            }
            if (rowAmountToWin == playStoneCounterIsDiagonal) {
                points += getPoints(currentPlayer, turnMultiplier)
            }

            wonPositions.clear()
            playStoneCounterIsDiagonal = 0

            x = positionX
            y = positionY
            while (x >= 0 && y < 6) {
                if (testboard[x][y] == currentPlayer) {
                    playStoneCounterIsDiagonal++
                    wonPositions.add(Pair(x, y))
                }
                if (testboard[x][y] != currentPlayer) {
                    break
                }
                x--
                y++
            }
            x = positionX + 1
            y = positionY - 1
            while (x < 7 && y >= 0 && y < 6) {
                if (testboard[x][y] == currentPlayer) {
                    playStoneCounterIsDiagonal++
                    wonPositions.add(Pair(x, y))
                }
                if (testboard[x][y] != currentPlayer) {
                    break
                }
                x++
                y--
            }
            if (rowAmountToWin == playStoneCounterIsDiagonal - 1) {
                points += getPoints(currentPlayer, turnMultiplier, tripleWarner = true)
            }
            if (rowAmountToWin == playStoneCounterIsDiagonal) {
                points += getPoints(currentPlayer, turnMultiplier)
            }

            // XY diagonal end

            playStoneCounterIsDiagonal = 0
            wonPositions.clear()

            if (rowAmountToWin == playStoneCounterIsDiagonal - 1) {
                points += getPoints(currentPlayer, turnMultiplier, tripleWarner = true)
            }
            if (rowAmountToWin == playStoneCounterIsDiagonal) {
                points += getPoints(currentPlayer, turnMultiplier)
            }

            wonPositions.clear()

            for (column in testboard) {
                for (cell in column) {
                    if (cell == 0) {
                        emptyCells++
                    }
                }
            }

            //check for Draw
            if (emptyCells - 1 == 0) {
                points += 0
            }
            points += 0
        }

        return points
    }

    private fun getPoints(
        currentPlayer: Int,
        turnMultiplier: Int,
        tripleWarner: Boolean = false
    ): Int {
        val lossPoints = -10
        val winPoints = 11
        val tripleLossPoints = -3
        val tripleWinPoints = 4
        if (tripleWarner) {
            return if (currentPlayer == 1) {
                tripleLossPoints * 1000 / turnMultiplier
            } else {
                tripleWinPoints * 1000 / turnMultiplier
            }
        }
        return if (currentPlayer == 1) {
            lossPoints * 1000 / turnMultiplier
        } else {
            winPoints * 1000 / turnMultiplier
        }
    }
}