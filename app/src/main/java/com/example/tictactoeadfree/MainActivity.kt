package com.example.tictactoeadfree

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tictactoeadfree.gameEngine.TicTacToeEngine
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), TicTacToeEngine.EndedGameListener {
    private val toe: TicTacToeEngine = TicTacToeEngine(listener = this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        intializeBoardListener()
    }

    private fun getCurrentPlayerPlayStone(): String {
        return if (toe.getCurrentPlayer() == 1) "X" else "O"
    }

    private fun intializeBoardListener() {
        one_one.setOnClickListener{
            toe.playerTurn(0,2)
            one_one.text = getCurrentPlayerPlayStone()
            one_one.setOnClickListener{}
        }

        one_two.setOnClickListener{
            toe.playerTurn(1,2)
            one_two.text = getCurrentPlayerPlayStone()
            one_two.setOnClickListener{}
        }

        one_three.setOnClickListener{
            toe.playerTurn(2,2)
            one_three.text = getCurrentPlayerPlayStone()
            one_three.setOnClickListener{}
        }

        two_one.setOnClickListener{
            toe.playerTurn(0,1)
            two_one.text = getCurrentPlayerPlayStone()
            two_one.setOnClickListener{}
        }

        two_two.setOnClickListener{
            toe.playerTurn(1,1)
            two_two.text = getCurrentPlayerPlayStone()
            two_two.setOnClickListener{}
        }

        two_three.setOnClickListener{
            toe.playerTurn(2,1)
            two_three.text = getCurrentPlayerPlayStone()
            two_three.setOnClickListener{}
        }

        three_one.setOnClickListener{
            toe.playerTurn(0,0)
            three_one.text = getCurrentPlayerPlayStone()
            three_one.setOnClickListener{}
        }

        three_two.setOnClickListener{
            toe.playerTurn(1,0)
            three_two.text = getCurrentPlayerPlayStone()
            three_two.setOnClickListener{}
        }

        three_three.setOnClickListener{
            toe.playerTurn(2,0)
            three_three.text = getCurrentPlayerPlayStone()
            three_three.setOnClickListener{}
        }

        clear_board.setOnClickListener{
            intializeBoardListener()
            toe.initializeBoard()
            one_one.text = ""
            one_two.text = ""
            one_three.text = ""
            two_one.text = ""
            two_two.text = ""
            two_three.text = ""
            three_one.text = ""
            three_two.text = ""
            three_three.text = ""
        }
    }

    override fun onPlayerWin() {
        game_info.text = "Sieg!"
    }

    override fun onDraw() {
        game_info.text = "Unentschieden!"
    }

    override fun onSwitchPlayer(playerNumber: Int) {
        game_info.text = playerNumber.toString()
    }

    override fun onInitializeBoard() {
        game_info.text = "Los Geht's!"
    }
}