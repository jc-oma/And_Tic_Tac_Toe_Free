package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.tictactoeadfree.R
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
                four_in_a_row_game_end_overlay.onGameWon(it.wonPlayer)
            }, 1200)
        }
    }

    private fun initListener() {
        four_in_a_row_game_end_overlay.setOnClickListener {
            four_in_a_row_game_end_overlay.isVisible = false
        }
    }
}