package com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleXOBoard

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameDifficulty
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameMode
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerMode
import com.jcDevelopment.tictactoeadfree.module.gameDificulty.GameOpponentUtils
import com.jcDevelopment.tictactoeadfree.module.gameEngine.tictactoe.TicTacToeEngine
import com.jcDevelopment.tictactoeadfree.module.sounds.SoundPlayer
import com.jcDevelopment.tictactoeadfree.module.statistics.StatisticsUtils
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameStatisticsViewModel
import com.jcDevelopment.tictactoeadfree.module.viewmodels.MultiplayerSettingsViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_board_two_dimensions_simple.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class SimpleTicTacToeBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), TicTacToeEngine.GameListener, KoinComponent {
    init {
        initView(context)
    }

    val backPressEvent: PublishSubject<Boolean> = PublishSubject.create<Boolean>()
    val showInterstitialAd: PublishSubject<Boolean> = PublishSubject.create<Boolean>()
    val isGameEnded: PublishSubject<Boolean> = PublishSubject.create<Boolean>()

    private val multiplayerSettingsViewModel by inject<MultiplayerSettingsViewModel>()
    private val gameSettingsViewModel by inject<GameSettingsViewModel>()
    private val statisticsViewModel by inject<GameStatisticsViewModel>()

    private val oImgPlayerStone =
        ContextCompat.getDrawable(context, R.drawable.blender_o_play_stone)
    private val xImgPlayerStone =
        ContextCompat.getDrawable(context, R.drawable.blender_x_play_stone)

    private val whobbleAnimation by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.whobble_animation_little
        )
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_board_two_dimensions_simple, this)
    }

    private var isGameOver: Boolean = false

    private val toe: TicTacToeEngine =
        TicTacToeEngine(
            listener = this
        )

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

    private val toBeAnimatedViews: List<View> by lazy {
        listOf(
            three_one,
            three_two,
            three_three,
            two_one,
            two_two,
            two_three,
            one_one,
            one_two,
            one_three,
            simple_2d_thinking_witch,
            restart_game_button
        )
    }

    //Todo make dynamic
    private val grid = 3

    private val placeHolderDrawable =
        ContextCompat.getDrawable(context, R.drawable.ic_spooky_kurbis_v3_3d)

    override fun onFinishInflate() {
        super.onFinishInflate()
        onInitializeBoard()
        restartBoard()
        prepareBoardStart()
    }

    override fun onSwitchPlayer(playerNumber: Int) {
        game_info.text = playerNumber.toString()
    }

    override fun onInitializeBoard() {
        val difficulty =
            GameDifficulty.valueOf(gameSettingsViewModel.getGameSettings().last().difficulty)
        simple_2d_thinking_witch.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                GameOpponentUtils.getAiOpponentList(difficulty)
            )
        )

        addViewsToHardwareLayer()

        simple_two_dim_tic_opponent_left_game_info?.backPressEvent?.subscribe {
            if (it) {
                backPressEvent.onNext(true)
            }
        }
    }

    private fun prepareBoardStart() {
        checkIfPlayerIsPlayerOne()
        prepareStartAnimations()
    }

    private fun checkIfPlayerIsPlayerOne() {
        val gameMode = multiplayerSettingsViewModel.getMultiplayerSettings().last().multiplayerMode
        val isHost = multiplayerSettingsViewModel.getMultiplayerSettings().last().isHost

        if (gameMode == MultiplayerMode.BLUETOOTH.toString()
        ) {
            toe.initMultiplayerListener()

            if (!isHost) {
                opponentIsTurning()
            }
        }
    }

    private fun addViewsToHardwareLayer() {
        toBeAnimatedViews.forEach {
            it.setLayerType(LAYER_TYPE_HARDWARE, null)
        }
    }

    override fun onOpponentIsTurning() {
        opponentIsTurning()
    }

    override fun onPlayerTurned(
        positionX: Int,
        positionY: Int,
        positionZ: Int,
        currentPlayer: Int
    ) {
        opponentHasTurned(positionX, positionY, currentPlayer)
    }

    override fun onRestartGame() {
        restartBoard()
        simple_two_dim_tic_game_end_overlay.isVisible = false
        isGameEnded.onNext(false)
    }

    override fun onGameEnd(
        wonPlayer: Int,
        wonPosition: MutableList<Triple<Int, Int, Int>>?
    ) {
        isGameOver = true
        if (wonPosition != null && wonPosition.isNotEmpty()) {
            val objectAnimatorList: MutableList<ObjectAnimator> =
                setupObejectAnimatorListForWinAnimation(wonPosition)

            val animSet = setupAnimatorSetForWinAnimation(objectAnimatorList, wonPlayer)
            animSet.start()
        } else winOverlayPreparation(wonPlayer)
    }

    override fun onOpponentLeft() {
        disableClicksInBoard()
        simple_two_dim_tic_opponent_left_game_info?.visibility = View.VISIBLE
    }

    private fun opponentIsTurning() {
        disableClicksInBoard()
        animateThinkingOpponent()
    }

    private fun disableClicksInBoard() {
        playGroundViewGrid.forEach { cellView ->
            cellView.isClickable = false
        }
    }

    private fun opponentHasTurned(
        positionX: Int,
        positionY: Int,
        currentPlayer: Int
    ) {
        val playedIndexInGrid = positionX + (positionY * grid)
        playGroundViewGrid[playedIndexInGrid].setImageDrawable(
            getCurrentPlayerPlayStone(
                currentPlayer
            )
        )
        playGroundViewGrid[playedIndexInGrid].setOnClickListener { }
        playGroundViewGrid[playedIndexInGrid].clearAnimation()
        playGroundViewGrid.forEach { cellView ->
            cellView.isClickable = true
        }

        clearThinkingOpponentAnimation()
    }

    private fun prepareStartAnimations() {
        var delayTimer: Long = 0
        val delayRange = 400
        playGroundViewGrid.forEach { cell ->
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

    private val playedGamesToInterstitialAd = 3

    private fun restartBoard() {
        isGameOver = false
        toe.initializeBoard()

        for ((index, cellView) in playGroundViewGrid.withIndex()) {
            startWhobbleAnimation(cellView)
            cellView.setImageDrawable(placeHolderDrawable)
            initializeClickListenerForPlayerTurn(cellView, index)
        }

        restart_game_button.clearAnimation()

        restart_game_button.setOnClickListener {
            restartBoard()
            toe.initializeBoard()
            for (cellView in playGroundViewGrid) {
                cellView.setImageDrawable(placeHolderDrawable)
            }
        }

        val multiplayerMode =
            multiplayerSettingsViewModel.getMultiplayerSettings().last().multiplayerMode
        if (multiplayerMode == MultiplayerMode.BLUETOOTH.toString() || multiplayerMode == MultiplayerMode.WIFI.toString()) {
            restart_game_button.isVisible = false
            simple_two_dim_tic_game_end_overlay.setOnClickListener {

                showInterstitialAd()

                simple_two_dim_tic_game_end_overlay.isVisible = false
                isGameEnded.onNext(false)
                restartBoard()
            }
        } else {
            restart_game_button.isVisible = true
            simple_two_dim_tic_game_end_overlay.setOnClickListener {
                showInterstitialAd()

                simple_two_dim_tic_game_end_overlay.isVisible = false
                isGameEnded.onNext(false)
            }
        }
    }

    private fun showInterstitialAd() {
        val playedGames = statisticsViewModel.getGameStatisticsList().size
        if (playedGames % playedGamesToInterstitialAd == 0) {
            showInterstitialAd.onNext(true)
        }
    }

    private fun initializeClickListenerForPlayerTurn(cellView: ImageView, index: Int) {
        cellView.setOnClickListener {
            if (!isGameOver) {
                toe.gameTurn(index % grid, index / grid, isRemoteTurn = false)
            }
        }
    }

    private fun getCurrentPlayerPlayStone(currentPlayer: Int): Drawable? {
        return if (currentPlayer == 1) xImgPlayerStone else oImgPlayerStone
    }

    private fun startWhobbleAnimation(view: View) {
        val loadAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.whobble_animation_little
        )
        val randomDuration = getRandomDuration()
        loadAnimation.duration = randomDuration

        view.startAnimation(
            loadAnimation
        )
    }

    private fun getRandomDuration() = ((Math.random() + 2) * 100).toLong()

    private fun winOverlayPreparation(wonPlayer: Int) {
        val drawablePair = StatisticsUtils(context).getDrawablesPair(GameMode.TIC_TAC_TOE)
        if (wonPlayer != 0) {
            game_info.text = context.getString(R.string.player_x_won, wonPlayer.toString())
            simple_two_dim_tic_game_end_overlay.onGameWon(
                wonPlayer,
                drawablePair,
                GameMode.TIC_TAC_TOE
            )
        } else {
            game_info.text = context.getString(R.string.draw)
            simple_two_dim_tic_game_end_overlay.onGameWon(
                wonPlayer,
                drawablePair,
                GameMode.TIC_TAC_TOE
            )
        }
        for (cellView in playGroundViewGrid) {
            cellView.clearAnimation()
        }
        simple_two_dim_tic_game_end_overlay.isVisible = true
        isGameEnded.onNext(true)
        restart_game_button.startAnimation(whobbleAnimation)
        deleteBoardListener()
    }

    private fun deleteBoardListener() {
        groupIds.forEach { id ->
            rootView.findViewById<View>(id).setOnClickListener {
                game_info.text = context.getString(R.string.game_has_ended_hint)
                restart_game_button.startAnimation(
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

    private fun animateThinkingOpponent() {
        simple_2d_thinking_witch.alpha = 1f
        val thinkingAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.thinking_ai_on_board_appear
        )
        simple_2d_thinking_witch.startAnimation(thinkingAnimation)

        playThinkingOpponentSound()
    }

    private fun playThinkingOpponentSound() {
        context?.let {
            val difficulty =
                GameDifficulty.valueOf(gameSettingsViewModel.getGameSettings().last().difficulty)
            SoundPlayer.getInstance(it).playLoadedSound(
                GameOpponentUtils.getOpponentSoundId(
                    difficulty
                )
            )
        }
    }

    private fun clearThinkingOpponentAnimation() {
        simple_2d_thinking_witch.clearAnimation()
        simple_2d_thinking_witch.alpha = 0f
    }
}