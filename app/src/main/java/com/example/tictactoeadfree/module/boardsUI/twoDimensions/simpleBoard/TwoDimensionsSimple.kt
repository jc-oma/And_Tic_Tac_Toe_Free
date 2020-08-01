package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleBoard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.gameEngine.TicTacToeEngine
import kotlinx.android.synthetic.main.board_two_dimensions_simple.view.*

class TwoDimensionsSimple @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr), TicTacToeEngine.GameListener {

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.board_two_dimensions_simple, this)
    }

    private val toe: TicTacToeEngine =
        TicTacToeEngine(listener = this)

    override fun onFinishInflate() {
        super.onFinishInflate()
        onInitializeBoard()
        intializeBoardListener()
    }

    private val groupIds = board_view_group.referencedIds

    private fun getCurrentPlayerPlayStone(): String {
        return if (toe.getCurrentPlayer() == 1) "X" else "O"
    }

    private fun intializeBoardListener() {
        game_end_overlay.setOnClickListener{
          game_end_overlay.isVisible = false
        }

        one_one.setOnClickListener{
            toe.playerTurn(0,2)
            one_one.text = getCurrentPlayerPlayStone()
            one_one.setOnClickListener{}
        }

        one_two.setOnClickListener{
            toe.playerTurn(1,2)
            one_two.text = getCurrentPlayerPlayStone()
            one_two.setOnClickListener{}
        }

        one_three.setOnClickListener{
            toe.playerTurn(2,2)
            one_three.text = getCurrentPlayerPlayStone()
            one_three.setOnClickListener{}
        }

        two_one.setOnClickListener{
            toe.playerTurn(0,1)
            two_one.text = getCurrentPlayerPlayStone()
            two_one.setOnClickListener{}
        }

        two_two.setOnClickListener{
            toe.playerTurn(1,1)
            two_two.text = getCurrentPlayerPlayStone()
            two_two.setOnClickListener{}
        }

        two_three.setOnClickListener{
            toe.playerTurn(2,1)
            two_three.text = getCurrentPlayerPlayStone()
            two_three.setOnClickListener{}
        }

        three_one.setOnClickListener{
            toe.playerTurn(0,0)
            three_one.text = getCurrentPlayerPlayStone()
            three_one.setOnClickListener{}
        }

        three_two.setOnClickListener{
            toe.playerTurn(1,0)
            three_two.text = getCurrentPlayerPlayStone()
            three_two.setOnClickListener{}
        }

        three_three.setOnClickListener{
            toe.playerTurn(2,0)
            three_three.text = getCurrentPlayerPlayStone()
            three_three.setOnClickListener{}
        }

        restart_game.setOnClickListener{
            intializeBoardListener()
            board_view_group.isEnabled = true
            toe.initializeBoard()
            one_one.text = ""
            one_two.text = ""
            one_three.text = ""
            two_one.text = ""
            two_two.text = ""
            two_three.text = ""
            three_one.text = ""
            three_two.text = ""
            three_three.text = ""
        }
    }

    override fun onPlayerWin() {
        game_info.text = context.getString(R.string.won)
        game_end_overlay.isVisible = true
        game_end_overlay.onGameWon()
        deleteBoardListener()
    }

    override fun onDraw() {
        game_info.text = context.getString(R.string.draw)
        game_end_overlay.isVisible = true
        game_end_overlay.onGameDraw()
        deleteBoardListener()
    }

    private fun deleteBoardListener() {
        groupIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener {
                game_info.text = context.getString(R.string.game_has_ended_hint)
            }
        }
    }

    override fun onSwitchPlayer(playerNumber: Int) {
        game_info.text = playerNumber.toString()
    }

    override fun onInitializeBoard() {
        game_info.text = context.getString(R.string.get_it_started)
    }
}