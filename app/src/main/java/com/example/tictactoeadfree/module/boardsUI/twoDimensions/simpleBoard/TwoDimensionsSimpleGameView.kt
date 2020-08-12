package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleBoard

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.gameEngine.TicTacToeEngine
import kotlinx.android.synthetic.main.view_board_two_dimensions_simple.view.*

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
        View.inflate(context, R.layout.view_board_two_dimensions_simple, this)
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

    private val placeHolderDrawable = context.getDrawable(R.drawable.ic_spooky_kurbis_v3_3d)

    override fun onFinishInflate() {
        super.onFinishInflate()
        onInitializeBoard()
        restartBoard()
    }

    fun prepareBoardStartAnimations() {
        var delayTimer: Long = 0
        val delayRange = 400
        playGroundViewGrid.forEach { cell ->
            val whobbleAnimation = AnimationUtils.loadAnimation(
                context,
                R.anim.whobble_animation
            )
            val fallDownAnimation = AnimationUtils.loadAnimation(
                context,
                R.anim.grid_fall_down_animation
            )
            fallDownAnimation.startOffset = delayTimer
            fallDownAnimation.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationRepeat(p0: Animation?) {
                }
                override fun onAnimationEnd(p0: Animation?) {
                    whobbleAnimation.duration = getRandomDuration()
                    cell.startAnimation(whobbleAnimation)
                }
                override fun onAnimationStart(p0: Animation?) {
                }
            })

            delayTimer += (Math.random() * delayRange).toLong()
            cell.alpha = 1f
            cell.startAnimation(
                fallDownAnimation
            )
        }
    }

    private val groupIds = board_view_group.referencedIds

    private fun getCurrentPlayerPlayStone(isAiTurn: Boolean = false): Drawable? {
        val currentPlayer = toe.getCurrentPlayer()
        return if (currentPlayer == 1 && !isAiTurn) context.getDrawable(xImgPlayerStone) else context.getDrawable(
            oImgPlayerStone
        )
    }

    private fun restartBoard() {
        game_end_overlay.setOnClickListener {
            game_end_overlay.isVisible = false
        }

        for ((index, cellView) in playGroundViewGrid.withIndex()) {
            startWhobbleAnimation(cellView)
            cellView.setImageDrawable(placeHolderDrawable)
            initializeClickListenerForPlayerTurn(cellView, index)
        }

        restart_game.whobbleAnimation(false)

        restart_game.setOnTouchListener { view, motionEvent ->
            restart_game.changeStyleOnTouchEvent(motionEvent)
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                view.performClick()
                restartBoard()
                toe.initializeBoard()
                for (cellView in playGroundViewGrid) {
                    cellView.setImageDrawable(placeHolderDrawable)
                }
            }
            return@setOnTouchListener true
        }
    }

    private fun initializeClickListenerForPlayerTurn(cellView: ImageView, index: Int) {
        cellView.setOnClickListener {
            onPlayerTurned(index, cellView)
        }
    }

    private fun onPlayerTurned(index: Int, cellView: ImageView, isAiTurn: Boolean = false) {
        toe.gameTurn(index % grid, index / grid)
        cellView.setImageDrawable(getCurrentPlayerPlayStone(isAiTurn))
        cellView.clearAnimation()
        cellView.setOnClickListener {}
    }

    private fun startWhobbleAnimation(view: View) {
        val loadAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.whobble_animation
        )
        val randomDuration = getRandomDuration()
        loadAnimation.duration = randomDuration

        view.startAnimation(
            loadAnimation
        )
    }

    private fun getRandomDuration() = ((Math.random() + 2) * 100).toLong()

    override fun onGameEnd(wonPlayer: Int) {
        if (wonPlayer != 0) {
            game_info.text = context.getString(R.string.player_x_won, wonPlayer.toString())
            game_end_overlay.onGameWon(wonPlayer)
        } else {
            game_info.text = context.getString(R.string.draw)
            game_end_overlay.onGameWon(wonPlayer)
        }
        for (cellView in playGroundViewGrid) {
            cellView.clearAnimation()
        }
        game_end_overlay.isVisible = true
        restart_game.whobbleAnimation(true)
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

    override fun onAiIsTurning() {
        playGroundViewGrid.forEach{ cellView ->
            cellView.isClickable = false
        }
    }

    override fun onAiTurned(positionX: Int, positionY: Int, positionZ: Int) {
        val playedIndexInGrid = positionX + (positionY * grid)
        onPlayerTurned(playedIndexInGrid, playGroundViewGrid[playedIndexInGrid], true)
        playGroundViewGrid.forEach{ cellView ->
            cellView.isClickable = true
        }
    }
}