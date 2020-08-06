package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleBoard

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.gameEngine.TicTacToeEngine
import kotlinx.android.synthetic.main.board_two_dimensions_simple.view.*

class TwoDimensionsSimpleGameView @JvmOverloads constructor(
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

    private val playGroundViewGrid: List<ImageView> by lazy {
        listOf(
            three_one,
            three_two,
            three_three,
            two_one,
            two_two,
            two_three,
            one_one,
            one_two,
            one_three
        )
    }

    //Todo make dynamic
    private val grid = 3

    override fun onFinishInflate() {
        super.onFinishInflate()
        onInitializeBoard()
        intializeBoardListener()
    }

    private val groupIds = board_view_group.referencedIds

    private fun getCurrentPlayerPlayStone(): Drawable? {
        return if (toe.getCurrentPlayer() == 1) context.getDrawable(xImgPlayerStone) else context.getDrawable(
            oImgPlayerStone
        )
    }

    private fun intializeBoardListener() {
        game_end_overlay.setOnClickListener {
            game_end_overlay.isVisible = false
        }

        for ((index, cellView) in playGroundViewGrid.withIndex()) {
            cellView.setOnClickListener {
                toe.playerTurn(index % grid, index / grid)
                cellView.setImageDrawable(getCurrentPlayerPlayStone())
                cellView.setOnClickListener {}
            }
        }

        restart_game.setOnTouchListener { view, motionEvent ->
            restart_game.changeStyleOnTouchEvent(motionEvent)
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                view.performClick()
                intializeBoardListener()
                board_view_group.isEnabled = true
                toe.initializeBoard()
                val placeHolderDrawable = context.getDrawable(R.drawable.blender_box_placeholder)
                for (cellView in playGroundViewGrid) {
                    cellView.setImageDrawable(placeHolderDrawable)
                }
            }
            return@setOnTouchListener true
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