package com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.view.clicks
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameMode
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerMode
import com.jcDevelopment.tictactoeadfree.module.sounds.SoundPlayer
import com.jcDevelopment.tictactoeadfree.module.statistics.StatisticsUtils
import com.jcDevelopment.tictactoeadfree.module.viewmodels.MultiplayerSettingsViewModel
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_simple_four_in_a_row_board.*
import org.koin.android.ext.android.inject


class SimpleFourInARowBoardFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() = SimpleFourInARowBoardFragment()
    }

    private var opponentIsTurningDisposable: Disposable? = null
    private var gameEndDisposable: Disposable? = null
    private var restartDisposable: Disposable? = null
    private var backpressedDisposable: Disposable? = null
    private var oponentLeftDisposable: Disposable? = null
    private val multiplayerSettingsViewModel by inject<MultiplayerSettingsViewModel>()

    private val isOnlineGame = multiplayerSettingsViewModel.getMultiplayerSettings()
        .last().multiplayerMode == MultiplayerMode.BLUETOOTH.toString() || multiplayerSettingsViewModel.getMultiplayerSettings()
        .last().multiplayerMode == MultiplayerMode.WIFI.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_four_in_a_row_board, container, false)
    }

    override fun onResume() {
        super.onResume()

        initListener()
    }

    override fun onPause() {
        super.onPause()
        disposeOnPause()
    }

    private fun disposeOnPause() {
        opponentIsTurningDisposable?.dispose()
        gameEndDisposable?.dispose()
        restartDisposable?.dispose()
        backpressedDisposable?.dispose()
        oponentLeftDisposable?.dispose()
    }

    private fun whobbleRestartButton(isWhobbling: Boolean) {
        if (isWhobbling) {
            val whobbleAnimation = AnimationUtils.loadAnimation(
                context,
                R.anim.whobble_animation_little
            )
            four_in_a_row_button_text?.startAnimation(whobbleAnimation)
            four_in_a_row_button?.startAnimation(whobbleAnimation)
        } else {
            four_in_a_row_button_text?.clearAnimation()
            four_in_a_row_button?.clearAnimation()
        }
    }

    private fun initListener() {
        //listen when opponent left game
        oponentLeftDisposable = fragment_four_in_a_row_playboard?.opponentLeftEvent?.doOnNext { isOpponentGone ->
            if (isOpponentGone) {
                four_in_a_row_opponent_left_game_info?.isVisible = true
            }
        }?.subscribe()

        four_in_a_row_game_end_overlay?.setOnClickListener {
            four_in_a_row_game_end_overlay?.isVisible = false

            if (isOnlineGame) {
                fragment_four_in_a_row_playboard?.restartBoard()
            }
        }

        backpressedDisposable = four_in_a_row_opponent_left_game_info?.backPressEvent?.subscribe {
            if (it) {
                this.activity?.onBackPressed()
            }
        }


        val throttleDuration: Long = 5000

        if (isOnlineGame) {
            four_in_a_row_button_text?.isVisible = false
            four_in_a_row_button_text?.isClickable = false
        } else {
            restartDisposable = four_in_a_row_button_text.clicks()
                .throttleFirst(throttleDuration, java.util.concurrent.TimeUnit.MILLISECONDS)
                .subscribe {
                    fragment_four_in_a_row_playboard.restartBoard()
                    whobbleRestartButton(false)
                }

            four_in_a_row_button_text?.isVisible = true
            four_in_a_row_button_text?.isClickable = true
        }

        //check weather game ended
        gameEndDisposable = fragment_four_in_a_row_playboard.appEventFlowable.subscribe {
            Handler().postDelayed({
                four_in_a_row_game_end_overlay?.isVisible = true
                four_in_a_row_game_end_overlay?.onGameWon(
                    it.wonPlayer,
                    StatisticsUtils(context).getDrawablesPair(GameMode.FOUR_IN_A_ROW),
                    GameMode.FOUR_IN_A_ROW
                )
                whobbleRestartButton(true)
            }, 1200)
        }

        opponentIsTurningDisposable = fragment_four_in_a_row_playboard?.onOpponentIsTurning?.doOnNext {
            if (it) {
                animateThinkingOpponent()
                playThinkingOpponentSound()
            } else {
                clearThinkingOpponentAnimation()
            }
        }?.subscribe()
    }

    private fun playThinkingOpponentSound() {
        SoundPlayer(context).playSound(R.raw.mp3_frankenstein_thinking)
    }

    private fun animateThinkingOpponent() {
        simple_2d_thinking_frankenstein?.alpha = 1f
        val thinkingAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.thinking_ai_on_board_appear
        )
        simple_2d_thinking_frankenstein?.startAnimation(thinkingAnimation)
    }

    private fun clearThinkingOpponentAnimation() {
        simple_2d_thinking_frankenstein?.clearAnimation()
        simple_2d_thinking_frankenstein?.alpha = 0f
    }
}