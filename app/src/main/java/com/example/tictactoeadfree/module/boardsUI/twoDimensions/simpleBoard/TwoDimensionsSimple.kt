package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleBoard

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.gameEngine.TicTacToeEngine
import kotlinx.android.synthetic.main.board_two_dimensions_simple.view.*

class TwoDimensionsSimple @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), TicTacToeEngine.GameListener {

    init {
        initView(context)
    }

    @DrawableRes
    private val oImgPlayerStone = R.drawable.blender_o_play_stone
    private val xImgPlayerStone = R.drawable.blender_x_play_stone

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

    private fun getCurrentPlayerPlayStone(): Drawable? {
        return if (toe.getCurrentPlayer() == 1) context.getDrawable(xImgPlayerStone) else context.getDrawable(oImgPlayerStone)
    }

    private fun intializeBoardListener() {
        game_end_overlay.setOnClickListener {
            game_end_overlay.isVisible = false
        }

        one_one.setOnClickListener {
            toe.playerTurn(0, 2)
            one_one.setImageDrawable(getCurrentPlayerPlayStone())
            one_one.setOnClickListener {}
        }

        one_two.setOnClickListener {
            toe.playerTurn(1, 2)
            one_two.setImageDrawable(getCurrentPlayerPlayStone())
            one_two.setOnClickListener {}
        }

        one_three.setOnClickListener {
            toe.playerTurn(2, 2)
            one_three.setImageDrawable(getCurrentPlayerPlayStone())
            one_three.setOnClickListener {}
        }

        two_one.setOnClickListener {
            toe.playerTurn(0, 1)
            two_one.setImageDrawable(getCurrentPlayerPlayStone())
            two_one.setOnClickListener {}
        }

        two_two.setOnClickListener {
            toe.playerTurn(1, 1)
            two_two.setImageDrawable(getCurrentPlayerPlayStone())
            two_two.setOnClickListener {}
        }

        two_three.setOnClickListener {
            toe.playerTurn(2, 1)
            two_three.setImageDrawable(getCurrentPlayerPlayStone())
            two_three.setOnClickListener {}
        }

        three_one.setOnClickListener {
            toe.playerTurn(0, 0)
            three_one.setImageDrawable(getCurrentPlayerPlayStone())
            three_one.setOnClickListener {}
        }

        three_two.setOnClickListener {
            toe.playerTurn(1, 0)
            three_two.setImageDrawable(getCurrentPlayerPlayStone())
            three_two.setOnClickListener {}
        }

        three_three.setOnClickListener {
            toe.playerTurn(2, 0)
            three_three.setImageDrawable(getCurrentPlayerPlayStone())
            three_three.setOnClickListener {}
        }

        restart_game.setOnClickListener {
            intializeBoardListener()
            board_view_group.isEnabled = true
            toe.initializeBoard()
            val kuerbisDrawable = context.getDrawable(R.drawable.blender_box_placeholder)
            one_one.setImageDrawable(kuerbisDrawable)
            one_two.setImageDrawable(kuerbisDrawable)
            one_three.setImageDrawable(kuerbisDrawable)
            two_one.setImageDrawable(kuerbisDrawable)
            two_two.setImageDrawable(kuerbisDrawable)
            two_three.setImageDrawable(kuerbisDrawable)
            three_one.setImageDrawable(kuerbisDrawable)
            three_two.setImageDrawable(kuerbisDrawable)
            three_three.setImageDrawable(kuerbisDrawable)
        }
    }

    override fun onPlayerWin(wonPlayer: Int) {
        game_info.text = context.getString(R.string.player_x_won, wonPlayer.toString())
        game_end_overlay.isVisible = true
        game_end_overlay.onGameWon(wonPlayer)
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
                restart_game.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.shake_animation
                    )
                )
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