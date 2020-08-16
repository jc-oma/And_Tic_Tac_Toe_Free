package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleBoard

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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

    override fun onSwitchPlayer(playerNumber: Int) {
        game_info.text = playerNumber.toString()
    }

    override fun onInitializeBoard() {
        game_info.text = context.getString(R.string.get_it_started)
    }

    override fun onAiIsTurning() {
        playGroundViewGrid.forEach { cellView ->
            cellView.isClickable = false
        }
        animateThinkingAi()
    }

    override fun onPlayerTurned(
        positionX: Int,
        positionY: Int,
        positionZ: Int,
        currentPlayer: Int
    ) {
        val playedIndexInGrid = positionX + (positionY * grid)
        playGroundViewGrid[playedIndexInGrid].setImageDrawable(getCurrentPlayerPlayStone(currentPlayer))
        playGroundViewGrid[playedIndexInGrid].setOnClickListener {  }
        playGroundViewGrid[playedIndexInGrid].clearAnimation()
        playGroundViewGrid.forEach { cellView ->
            cellView.isClickable = true
        }

        clearThinkingAiAnimation()
    }

    override fun onGameEnd(
        wonPlayer: Int,
        wonPosition: MutableList<Triple<Int, Int, Int>>?
    ) {
        if (wonPosition != null && wonPosition.isNotEmpty()) {
            val objectAnimatorList: MutableList<ObjectAnimator> =
                setupObejectAnimatorListForWinAnimation(wonPosition)

            val animSet = setupAnimatorSetForWinAnimation(objectAnimatorList, wonPlayer)
            animSet.start()
        } else winOverlayPreparation(wonPlayer)
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
            fallDownAnimation.setAnimationListener(object : Animation.AnimationListener {
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

    private fun restartBoard() {
        toe.initializeBoard()
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
            toe.gameTurn(index % grid, index / grid)
        }
    }

    private fun getCurrentPlayerPlayStone(currentPlayer: Int): Drawable? {
        return if (currentPlayer == 1) context.getDrawable(xImgPlayerStone) else context.getDrawable(
            oImgPlayerStone
        )
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

    private fun winOverlayPreparation(wonPlayer: Int) {
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

    private fun setupAnimatorSetForWinAnimation(
        objectAnimatorList: MutableList<ObjectAnimator>,
        wonPlayer: Int
    ): AnimatorSet {
        val animSet = AnimatorSet()
        animSet.playTogether(objectAnimatorList as Collection<Animator>?)
        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                winOverlayPreparation(wonPlayer)
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

        })
        return animSet
    }

    private fun setupObejectAnimatorListForWinAnimation(wonPosition: MutableList<Triple<Int, Int, Int>>): MutableList<ObjectAnimator> {
        val objectAnimatorList: MutableList<ObjectAnimator> = mutableListOf()
        wonPosition.forEach { pos ->
            val element = ObjectAnimator.ofFloat(
                playGroundViewGrid[pos.first + pos.second * grid],
                "rotationX",
                360f,
                0f
            )
            element.duration = 800

            objectAnimatorList.add(
                element
            )
        }
        return objectAnimatorList
    }

    private fun animateThinkingAi() {
        simple_2d_thinking_witch.alpha = 1f
        val thinkingAnimation =AnimationUtils.loadAnimation(
            context,
            R.anim.thinking_witch_appear
        )
        simple_2d_thinking_witch.startAnimation(thinkingAnimation)
    }

    private fun clearThinkingAiAnimation() {
        simple_2d_thinking_witch.clearAnimation()
        simple_2d_thinking_witch.alpha = 0f
    }
}