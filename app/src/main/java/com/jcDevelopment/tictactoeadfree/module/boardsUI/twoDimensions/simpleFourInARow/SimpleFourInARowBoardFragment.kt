package com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameMode
import com.jcDevelopment.tictactoeadfree.module.statistics.StatisticsUtils
import com.jakewharton.rxbinding4.view.clicks
import kotlinx.android.synthetic.main.fragment_simple_four_in_a_row_board.*

class SimpleFourInARowBoardFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() = SimpleFourInARowBoardFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_four_in_a_row_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()

        fragment_four_in_a_row_playboard.appEventFlowable.subscribe {
            Handler().postDelayed({
                four_in_a_row_game_end_overlay.isVisible = true
                four_in_a_row_game_end_overlay.onGameWon(
                    it.wonPlayer,
                    StatisticsUtils(context).getDrawablesPair(GameMode.FOUR_IN_A_ROW),
                    GameMode.FOUR_IN_A_ROW
                )
                whobbleRestartButton(true)
            }, 1200)
        }
    }

    private fun whobbleRestartButton(isWhobbling: Boolean) {
        if (isWhobbling) {
            val whobbleAnimation = AnimationUtils.loadAnimation(
                context,
                R.anim.whobble_animation_little
            )
            four_in_a_row_button_text.startAnimation(whobbleAnimation)
            four_in_a_row_button.startAnimation(whobbleAnimation)
        } else {
            four_in_a_row_button_text.clearAnimation()
            four_in_a_row_button.clearAnimation()
        }
    }

    private fun initListener() {
        four_in_a_row_game_end_overlay.setOnClickListener {
            four_in_a_row_game_end_overlay.isVisible = false
        }

        val throttleDuration: Long = 5000
        four_in_a_row_button_text.clicks()
            .throttleFirst(throttleDuration, java.util.concurrent.TimeUnit.MILLISECONDS).subscribe {
                fragment_four_in_a_row_playboard.restartBoard()
                whobbleRestartButton(false)
            }
    }
}