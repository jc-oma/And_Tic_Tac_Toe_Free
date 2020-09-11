package com.jcDevelopment.tictactoeadfree.module.home

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
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
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerSettings
import com.jcDevelopment.tictactoeadfree.module.gameDificulty.GameDifficultyChooserFragment
import com.jcDevelopment.tictactoeadfree.module.logo.LogoFragment
import com.jcDevelopment.tictactoeadfree.module.usedLibraries.UsedLibrariesFragment
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import com.jcDevelopment.tictactoeadfree.module.viewmodels.MultiplayerSettingsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeActivity : BaseActivity(), HomeFragment.Listener, LogoFragment.Listener,
    TwoPlayerModeChooserFragment.Listener, GameDifficultyChooserFragment.Listener {
    private var isTransactionSafe: Boolean = true
    private val activity = this
    private val manager = activity.supportFragmentManager

    //Bluetooth
    // Intent request codes
    private val REQUEST_ENABLE_BT_AS_HOST = 3
    private val REQUEST_ENABLE_BT_AS_CLIENT = 4

    //TODO Rename with context of Game
    /*** Name of the connected device*/
    private var mConnectedDeviceName: String? = null

    /*** String buffer for outgoing messages*/
    private val mOutStringBuffer: StringBuffer? = null

    /*** Local Bluetooth adapter*/
    private var mBluetoothAdapter: BluetoothAdapter? = null

    private val gameSettingsViewModel by inject<GameSettingsViewModel>()
    private val multiplayerSettingsViewModel by viewModel<MultiplayerSettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        openLogoFragment()

        MobileAds.initialize(this) {}

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        initToolbar()
    }

    override fun onResume() {
        super.onResume()

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (BlueToothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (BlueToothService?.getState() == BlueToothService.STATE_NONE) {
                // Start the Bluetooth chat services
                BlueToothService?.start()
            }
        }


        bluetooth_connection_status.setOnClickListener {
            BlueToothService?.write((Math.random().toString()).toByteArray())
        }

        BlueToothService.getConnectionObservable().subscribe({makeToast(it.toString() + "connectionObservable")}, {makeToast(it.toString() + "errorconnectionObservable")})
        BlueToothService.getDeviceNameObservable().subscribe({makeToast(it.toString() + "deviceNameObservable")}, {makeToast(it.toString() + "errordeviceNameObservable")})
        BlueToothService.getMessageObservable().subscribe({makeToast(it.toString()+ "connectionMessageObservable")}, {makeToast(it.toString() + "errorconnectionMessageObservable")})
        BlueToothService.getMessageToastObservable().subscribe({makeToast(it.toString()+ "toastnObservable")}, {makeToast(it.toString() + "errortoastnObservable")})
    }

    override fun onDestroy() {
        super.onDestroy()
        BlueToothService?.stop()
    }

    override fun onPostResume() {
        super.onPostResume()
        isTransactionSafe = true
    }

    override fun onPause() {
        super.onPause()
        isTransactionSafe = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT_AS_HOST && resultCode == RESULT_OK) {
            openBluetoothAsHost()
        } else if (requestCode == REQUEST_ENABLE_BT_AS_CLIENT && resultCode == RESULT_OK) {
            getBluetoothlist()
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.result_canceled_info), Toast.LENGTH_LONG).show()
        }
    }

    private fun makeToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }

    override fun onLogoFragmentLoaded() {
        openHomeFragment()
    }

    override fun onHomeFragmentGameStartButtonClick() {
        if (!getGameSettings().isSecondPlayerAi && isBluetoothAvailable()) {
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.add(R.id.main_activity_root, TwoPlayerModeChooserFragment.newInstance())
            transaction.commit()
        } else if (getGameSettings().isSecondPlayerAi) {
            openAiDifficultyChooserFragment()
        } else {
            openGameFragment()
        }
    }

    private fun openAiDifficultyChooserFragment() {
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(R.id.main_activity_root, GameDifficultyChooserFragment.newInstance())
        transaction.commit()
    }

    override fun onTwoPlayerModeChooserFragmentHotseatClick() {
        if (manager.fragments.size > 0) {
            manager.fragments.remove(manager.fragments.last())
        }
        openGameFragment()
    }

    override fun onCheckIfBluetoothAvailable(): Boolean {
        return isBluetoothAvailable()
    }

    override fun onBluetoothCreateHostButtonClicked() {
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_AS_HOST)
        } else {
            openBluetoothAsHost()
        }
    }

    override fun onBluetoothConnectToGameButtonClicked() {
        if (!mBluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT_AS_CLIENT)
        } else {
            getBluetoothlist()
        }
    }

    override fun onAiDifficultyChosen() {
        if (manager.fragments.size > 0) {
            manager.fragments.remove(manager.fragments.last())
        }
        openGameFragment()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        home_activity_bluetooth_list?.visibility = View.GONE

        if (manager.fragments.size > 1) {
            manager.beginTransaction().remove(manager.fragments.last()).commit()
        } else {
            this.finish()
        }
    }

    private fun openBluetoothAsHost() {
        multiplayerSettingsViewModel.updateMultiplayersettings(MultiplayerSettings(isHost = true))
        BlueToothService?.start()
    }

    private fun getBluetoothlist() {
        multiplayerSettingsViewModel.updateMultiplayersettings(MultiplayerSettings(isHost = false))
        val bondedDevices = mBluetoothAdapter?.bondedDevices
        val arrayAdapter = ArrayAdapter<String>(
            activity,
            R.layout.recyclerview_used_library,
            R.id.used_library_headline,
            bondedDevices!!.map { it.name.toString() + it.name })
        home_activity_bluetooth_list.adapter = arrayAdapter
        home_activity_bluetooth_list.setOnItemClickListener { _, _, position, _ ->
            home_activity_bluetooth_list.isVisible = false
            val bondedBluetoothAdapter = mBluetoothAdapter?.getRemoteDevice(
                bondedDevices.elementAt(
                    position
                ).address
            )

            BlueToothService?.connect(bondedBluetoothAdapter!!, secure = false)
        }

        home_activity_bluetooth_list.visibility = View.VISIBLE
    }

    private fun initToolbar() {
        home_toolbar.inflateMenu(R.menu.menu_fragment_home)
        home_toolbar.title = this.getText(R.string.app_name)
        home_toolbar.setTitleTextAppearance(this, R.style.berkshireTextStyle)

        home_toolbar.menu[0].setOnMenuItemClickListener {
            openLibraryFragment()
            return@setOnMenuItemClickListener true
        }

        home_toolbar.menu[1].setOnMenuItemClickListener {
            sharePlayStoreLink()
            return@setOnMenuItemClickListener true
        }
    }

    private fun sharePlayStoreLink() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getString(R.string.play_store_link))
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

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

    private fun openGameFragment() {
        val gameSettings = getGameSettings()
        val gameMode = GameMode.valueOf(gameSettings.gameMode)
        if (gameMode == GameMode.TIC_TAC_TOE) {
            openTwoDimensionalFragment()
        } else if (gameMode == GameMode.FOUR_IN_A_ROW) {
            openFourInARowFragment()
        }
    }

    private fun isBluetoothAvailable(): Boolean {
        return BluetoothAdapter.getDefaultAdapter() != null
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
        if (isTransactionSafe) {
            home_toolbar.isVisible = true
            val transaction: FragmentTransaction = manager.beginTransaction()
            transaction.replace(R.id.main_activity_root, HomeFragment.newInstance())
            transaction.commit()
        }
    }
}