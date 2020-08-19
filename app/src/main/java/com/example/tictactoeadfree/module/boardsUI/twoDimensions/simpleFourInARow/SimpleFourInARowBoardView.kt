package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.gameEngine.TicTacToeEngine

class SimpleFourInARowBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), TicTacToeEngine.GameListener {
    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_board_four_in_a_row_simple, this)
    }

    private val toe: TicTacToeEngine =
        TicTacToeEngine(listener = this)

    //TODO private val playGroundViewGrid: List<ImageView> by lazy { listOf() }

    //Todo make dynamic
    private val gridx = 7
    private val gridy = 6

    override fun onFinishInflate() {
        super.onFinishInflate()
        onInitializeBoard()
    }

    override fun onGameEnd(wonPlayer: Int, wonPosition: MutableList<Triple<Int, Int, Int>>?) {
        TODO("Not yet implemented")
    }

    override fun onSwitchPlayer(playerNumber: Int) {
    }

    override fun onInitializeBoard() {
    }

    override fun onAiIsTurning() {
    }

    override fun onPlayerTurned(
        positionX: Int,
        positionY: Int,
        positionZ: Int,
        currentPlayer: Int
    ) {

    }

}