package com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleXOBoard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.baseClasses.BaseFragment
import kotlinx.android.synthetic.main.fragment_two_dimensions_simple_game.*


class SimpleTicTacToeBoardFragment : BaseFragment() {
    companion object {
        @JvmStatic
        fun newInstance() =
            SimpleTicTacToeBoardFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_two_dimensions_simple_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        play_board.prepareBoardStart()
    }
}