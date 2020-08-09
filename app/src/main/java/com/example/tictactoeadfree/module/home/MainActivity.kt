package com.example.tictactoeadfree.module.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tictactoeadfree.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        play_board.onCreateBoardAnimations()
    }
}