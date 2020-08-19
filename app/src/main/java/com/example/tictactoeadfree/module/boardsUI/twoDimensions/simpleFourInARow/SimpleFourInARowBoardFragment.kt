package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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

        moveBoard()
    }

    private fun moveBoard() {
        val fallDownAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.move_for_in_a_row_a_bit
        )

        fragment_four_in_a_row_playboard.startAnimation(fallDownAnimation)
    }
}