package com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.blueToothService.BlueToothService
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerMode
import com.jcDevelopment.tictactoeadfree.module.gameEngine.fourInARow.FourInARowEngine
import com.jcDevelopment.tictactoeadfree.module.viewmodels.MultiplayerSettingsViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.processors.PublishProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.view_board_four_in_a_row_simple.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class SimpleFourInARowBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), FourInARowEngine.GameListener, KoinComponent {
    init {
        initView(context)
    }

    private val multiplayerSettingsViewModel by inject<MultiplayerSettingsViewModel>()

    //https://medium.com/@tylerwalker/event-based-systems-on-android-feat-rxjava-and-kotlin-7896279dfb07
    data class GameEndEvent(val wonPlayer: Int)

    val appEventProcessor: PublishProcessor<GameEndEvent> = PublishProcessor.create()
    val appEventFlowable = appEventProcessor as Flowable<GameEndEvent>

    val opponentLeftEvent: PublishSubject<Boolean> = PublishSubject.create<Boolean>()

    private fun initView(context: Context) {
        View.inflate(context, R.layout.view_board_four_in_a_row_simple, this)
    }
    private var isGameOver: Boolean = false
    private var isOpponentTurning: Boolean = false
    private val fourEngine: FourInARowEngine =
        FourInARowEngine(
            listener = this
        )

    private val playGroundViewGrid: List<SimpleFourInARowPlayGroundColumnView> by lazy {
        listOf(
            bord_row_1,
            bord_row_2,
            bord_row_3,
            bord_row_4,
            bord_row_5,
            bord_row_6,
            bord_row_7
        )
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        fourEngine.initializeBoard()
        initListener()

        checkIfPlayerIsPlayerOne()
    }

    fun restartBoard() {
        for (column in playGroundViewGrid) {
            column.restartBoard()
        }
        isGameOver = false

        fourEngine.initializeBoard()
    }

    private fun checkIfPlayerIsPlayerOne() {
        val gameMode = multiplayerSettingsViewModel.getMultiplayerSettings().last().multiplayerMode
        val isHost = multiplayerSettingsViewModel.getMultiplayerSettings().last().isHost

        if (gameMode == MultiplayerMode.BLUETOOTH.toString()
        ) {
            fourEngine.initMultiplayerListener()

            if (!isHost) {
                opponentIsTurning()
            }
        }
    }

    private fun initListener() {
        initRowClickListener()
    }

    private fun initRowClickListener() {
        for ((index, view) in playGroundViewGrid.withIndex()) {
            view.click.observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (!isOpponentTurning && !isGameOver) {
                        val toRow = fourEngine.getNextFreeYPosition(index)
                        if (toRow != null) {
                            fourEngine.gameTurn(index, false)
                        }
                    }
                }
        }
    }

    private fun opponentIsTurning() {
        isOpponentTurning = true
        animateThinkingOpponent()
    }

    private fun animateThinkingOpponent() {
        simple_2d_thinking_frankenstein.alpha = 1f
        val thinkingAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.thinking_ai_on_board_appear
        )
        simple_2d_thinking_frankenstein.startAnimation(thinkingAnimation)
    }

    private fun clearThinkingOpponentAnimation() {
        simple_2d_thinking_frankenstein.clearAnimation()
        simple_2d_thinking_frankenstein.alpha = 0f
    }

    override fun onGameEnd(wonPlayer: Int, wonPosition: MutableList<Pair<Int, Int>>?) {
        appEventProcessor.onNext(GameEndEvent(wonPlayer))

        isGameOver = true

        if (wonPosition != null) {
            for (position in wonPosition) {
                playGroundViewGrid[position.first].animateStackElement(position.second)
            }
        }
    }

    override fun onSwitchPlayer(playerNumber: Int) {
    }

    override fun onInitializeBoard() {
    }

    override fun onOpponentIsTurning() {
        opponentIsTurning()
    }

    override fun onPlayerTurned(
        positionX: Int,
        positionY: Int,
        currentPlayer: Int
    ) {
        isOpponentTurning = false
        playGroundViewGrid[positionX].animatePlayStoneDrop(positionY, currentPlayer)
        clearThinkingOpponentAnimation()
    }

    override fun onOpponentLeftGame() {
        opponentLeftEvent.onNext(true)
    }
}