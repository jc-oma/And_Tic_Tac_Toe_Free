package com.example.tictactoeadfree.module.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleBoard.TwoDimensionsSimpleGameFragment

class MainActivity : AppCompatActivity(), HomeFragment.Listener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openHomeFragment()
    }

    override fun onHomeFragmentButtonClick() {
        openTwoDimensionalFragment()
    }

    private fun openTwoDimensionalFragment() {
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.main_activity_root, TwoDimensionsSimpleGameFragment.newInstance())
        transaction.commit()
    }

    private fun openHomeFragment() {
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.main_activity_root, HomeFragment.newInstance())
        transaction.commit()
    }
}