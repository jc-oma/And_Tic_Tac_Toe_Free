package com.example.tictactoeadfree.module.home

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.baseClasses.BaseActivity
import com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleBoard.TwoDimensionsSimpleGameFragment
import com.example.tictactoeadfree.module.logo.LogoFragment

class MainActivity : BaseActivity(), HomeFragment.Listener, LogoFragment.Listener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openLogoFragment()
    }

    private fun openLogoFragment() {
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.main_activity_root, LogoFragment.newInstance())
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val manager = supportFragmentManager

        //LoadingScreen + 1
        if (manager.fragments.size > 2) {
            manager.beginTransaction().remove(manager.fragments.last()).commit()
        } else {
            this.finish()
        }
    }

    override fun onLogoFragmentLoaded() {
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