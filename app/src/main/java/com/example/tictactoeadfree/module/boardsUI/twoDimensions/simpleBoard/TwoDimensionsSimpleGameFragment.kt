package com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleBoard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tictactoeadfree.R
import kotlinx.android.synthetic.main.fragment_two_dimensions_simple_game.*


class TwoDimensionsSimpleGameFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            TwoDimensionsSimpleGameFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_two_dimensions_simple_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        play_board.prepareBoardStartAnimations()
    }
}