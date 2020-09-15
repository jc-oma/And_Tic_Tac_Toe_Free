package com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleXOBoard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.Gson
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.baseClasses.BaseFragment
import com.jcDevelopment.tictactoeadfree.module.blueToothService.BlueToothService
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerDataPackage.MultiplayerDataPackage
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

    override fun onResume() {
        super.onResume()
        play_board.backPressEvent.subscribe{
            if (it) {
                this.activity?.onBackPressed()
            }
        }
    }
}