package com.jcDevelopment.tictactoeadfree.module.home

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.ads.MobileAds
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.baseClasses.BaseActivity
import com.jcDevelopment.tictactoeadfree.module.blueToothService.BlueToothService
import com.jcDevelopment.tictactoeadfree.module.bluetoothSetUpUI.TwoPlayerModeChooserFragment
import com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow.SimpleFourInARowBoardFragment
import com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleXOBoard.TwoDimensionsSimpleGameFragment
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameMode
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettings
import com.jcDevelopment.tictactoeadfree.module.logo.LogoFragment
import com.jcDevelopment.tictactoeadfree.module.usedLibraries.UsedLibrariesFragment
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class HomeActivity : BaseActivity(), HomeFragment.Listener, LogoFragment.Listener,
    TwoPlayerModeChooserFragment.Listener {
    private val activity = this
    private val manager = activity.supportFragmentManager

    private val gameSettingsViewModel by inject<GameSettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openLogoFragment()

        MobileAds.initialize(this) {}

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        initToolbar()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //TODO REQUEST CODE from BluetoothService
        if (requestCode == 12 && resultCode == RESULT_OK) {
            Toast.makeText(this, "BlueTooth aktiv", Toast.LENGTH_LONG).show()
        }
    }

    private fun initToolbar() {
        home_toolbar.inflateMenu(R.menu.menu_fragment_home)
        home_toolbar.title = this.getText(R.string.app_name)
        home_toolbar.setTitleTextAppearance(this, R.style.berkshireTextStyle)

        home_toolbar.menu[0].setOnMenuItemClickListener {
            openLibraryFragment()
            return@setOnMenuItemClickListener true
        }
    }

    private fun openLibraryFragment() {
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.main_activity_root, UsedLibrariesFragment.newInstance())
        transaction.commit()
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

    override fun onHomeFragmentGameStartButtonClick() {
        if (!getGameSettings().isSecondPlayerAi) {
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.add(R.id.main_activity_root, TwoPlayerModeChooserFragment.newInstance())
            transaction.commit()
        } else {
            openGameFragment()
        }
    }

    private fun openGameFragment() {
        BlueToothService().checkForBluetoothAdapter(this)
        val gameSettings = getGameSettings()
        val gameMode = GameMode.valueOf(gameSettings.gameMode)
        if (gameMode == GameMode.TIC_TAC_TOE) {
            openTwoDimensionalFragment()
        } else if (gameMode == GameMode.FOUR_IN_A_ROW) {
            openFourInARowFragment()
        }
    }

    private fun getGameSettings(): GameSettings {
        return if (gameSettingsViewModel.getGameSettings().isEmpty()) {
            GameSettings()
        } else {
            gameSettingsViewModel.getGameSettings().last()
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
        home_toolbar.isVisible = true
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(R.id.main_activity_root, HomeFragment.newInstance())
        transaction.commit()
    }

    override fun onTwoPlayerModeChooserFragmentBluetoothClick() {
        if (manager.fragments.size > 0) {
            manager.fragments.remove(manager.fragments.last())
        }
        openGameFragment()
    }

    override fun onTwoPlayerModeChooserFragmentHotseatClick() {
        if (manager.fragments.size > 0) {
            manager.fragments.remove(manager.fragments.last())
        }
        openGameFragment()
    }
}