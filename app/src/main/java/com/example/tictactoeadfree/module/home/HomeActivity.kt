package com.example.tictactoeadfree.module.home

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.example.tictactoeadfree.R
import com.example.tictactoeadfree.module.baseClasses.BaseActivity
import com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow.SimpleFourInARowBoardFragment
import com.example.tictactoeadfree.module.boardsUI.twoDimensions.simpleXOBoard.TwoDimensionsSimpleGameFragment
import com.example.tictactoeadfree.module.data.gameSettings.GameMode
import com.example.tictactoeadfree.module.data.gameSettings.GameSettings
import com.example.tictactoeadfree.module.logo.LogoFragment
import com.example.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import com.google.android.gms.ads.MobileAds
import org.koin.android.ext.android.inject

class HomeActivity : BaseActivity(), HomeFragment.Listener, LogoFragment.Listener {
    private val activity = this
    private val manager = activity.supportFragmentManager

    private val gameSettingsViewModel by inject<GameSettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openLogoFragment()

        MobileAds.initialize(this) {}
    }

    private fun openLogoFragment() {
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.main_activity_root, LogoFragment.newInstance())
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (manager.fragments.size > 1) {
            manager.beginTransaction().remove(manager.fragments.last()).commit()
        } else {
            this.finish()
        }
    }

    override fun onLogoFragmentLoaded() {
        openHomeFragment()
    }

    override fun onHomeFragmentButtonClick() {
        val gameSettings = if (gameSettingsViewModel.getGameSettings().isEmpty()) {
            GameSettings()
        } else {
            gameSettingsViewModel.getGameSettings().last()
        }
        val gameMode = GameMode.valueOf(gameSettings.gameMode)
        if (gameMode == GameMode.TIC_TAC_TOE) {
            openTwoDimensionalFragment()
        } else if (gameMode == GameMode.FOUR_IN_A_ROW) {
            openFourInARowFragment()
        }
    }

    private fun openFourInARowFragment() {
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.main_activity_root, SimpleFourInARowBoardFragment.newInstance())
        transaction.commit()
    }

    private fun openTwoDimensionalFragment() {
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.main_activity_root, TwoDimensionsSimpleGameFragment.newInstance())
        transaction.commit()
    }

    private fun openHomeFragment() {
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(R.id.main_activity_root, HomeFragment.newInstance())
        transaction.commit()
    }
}