package com.jcDevelopment.tictactoeadfree.module.home

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Message
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
import com.jcDevelopment.tictactoeadfree.module.blueToothService.Constants
import com.jcDevelopment.tictactoeadfree.module.bluetoothSetUpUI.TwoPlayerModeChooserFragment
import com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow.SimpleFourInARowBoardFragment
import com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleXOBoard.TwoDimensionsSimpleGameFragment
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameMode
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettings
import com.jcDevelopment.tictactoeadfree.module.gameDificulty.GameDifficultyChooserFragment
import com.jcDevelopment.tictactoeadfree.module.logo.LogoFragment
import com.jcDevelopment.tictactoeadfree.module.usedLibraries.UsedLibrariesFragment
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class HomeActivity : BaseActivity(), HomeFragment.Listener, LogoFragment.Listener,
    TwoPlayerModeChooserFragment.Listener, GameDifficultyChooserFragment.Listener {
    private var isTransactionSafe: Boolean = true
    private val activity = this
    private val manager = activity.supportFragmentManager

    //Bluetooth
    // Intent request codes
    private val REQUEST_CONNECT_DEVICE_SECURE: Int = 1
    private val REQUEST_CONNECT_DEVICE_INSECURE = 2
    private val REQUEST_ENABLE_BT = 3

    //TODO Rename with context of Game
    /*** Name of the connected device*/
    private var mConnectedDeviceName: String? = null

    /*** String buffer for outgoing messages*/
    private val mOutStringBuffer: StringBuffer? = null

    /*** Local Bluetooth adapter*/
    private var mBluetoothAdapter: BluetoothAdapter? = null

    /*** Member object for the chat services*/
    private var mChatService: BlueToothService? = null

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constants.MESSAGE_STATE_CHANGE -> when (msg.arg1) {
                    BlueToothService.STATE_CONNECTED -> {
                        makeToast("connected")
                    }
                    BlueToothService.STATE_CONNECTING -> makeToast("connecting")
                    BlueToothService.STATE_LISTEN, BlueToothService.STATE_NONE -> bluetooth_connection_status.text =
                        "connecting"
                }
                Constants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    // construct a string from the buffer
                    val writeMessage = String(writeBuf)

                    bluetooth_connection_status.text = writeMessage
                }
                Constants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    // construct a string from the valid bytes in the buffer
                    val readMessage = String(readBuf, 0, msg.arg1)

                    bluetooth_connection_status.text = readMessage
                }
                Constants.MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString(Constants.DEVICE_NAME)
                    if (null != activity) {
                        bluetooth_connection_status.text = "Connected to $mConnectedDeviceName"
                    }
                }
                Constants.MESSAGE_TOAST -> if (null != activity) {
                    Toast.makeText(
                        activity, msg.data.getString(Constants.TOAST),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private val gameSettingsViewModel by inject<GameSettingsViewModel>()

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
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService?.getState() == BlueToothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService?.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mChatService?.stop()
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
        //TODO REQUEST CODE from BluetoothService
        if (requestCode == 12 && resultCode == RESULT_OK) {
            Toast.makeText(this, "BlueTooth aktiv", Toast.LENGTH_LONG).show()
        }
    }

    private fun makeToast(s: String) {
        Toast.makeText(this, mConnectedDeviceName.toString(), Toast.LENGTH_LONG).show()
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

    override fun onCheckIfBluetoothAvailable(): Boolean {
        return isBluetoothAvailable()
    }

    override fun onBluetoothCreateHostButtonClicked() {
        getBluetoothlist()
    }

    override fun onBluetoothConnectToGameButtonClicked() {
        getBluetoothlist()
    }

    override fun onAiDifficultyChosen() {
        if (manager.fragments.size > 0) {
            manager.fragments.remove(manager.fragments.last())
        }
        openGameFragment()
    }

    private fun getBluetoothlist() {
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

            mChatService = BlueToothService(activity, mHandler)
            mChatService?.connect(bondedBluetoothAdapter!!, secure = false)
        }
        home_activity_bluetooth_list.visibility = View.VISIBLE

        bluetooth_connection_status.setOnClickListener {
            mChatService?.write((Math.random().toString()).toByteArray())
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

    override fun onBackPressed() {
        super.onBackPressed()
        if (manager.fragments.size > 1) {
            manager.beginTransaction().remove(manager.fragments.last()).commit()
        } else {
            this.finish()
        }
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