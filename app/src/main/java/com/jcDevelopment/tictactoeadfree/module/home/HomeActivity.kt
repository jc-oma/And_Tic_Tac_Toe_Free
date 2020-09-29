package com.jcDevelopment.tictactoeadfree.module.home

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.jcDevelopment.tictactoeadfree.BuildConfig
import com.jcDevelopment.tictactoeadfree.R
import com.jcDevelopment.tictactoeadfree.module.baseClasses.BaseActivity
import com.jcDevelopment.tictactoeadfree.module.blueToothService.BlueToothService
import com.jcDevelopment.tictactoeadfree.module.bluetoothSetUpUI.TwoPlayerModeChooserFragment
import com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleFourInARow.SimpleFourInARowBoardFragment
import com.jcDevelopment.tictactoeadfree.module.boardsUI.twoDimensions.simpleXOBoard.SimpleTicTacToeBoardFragment
import com.jcDevelopment.tictactoeadfree.module.companyLogo.CompanyLogoFragment
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameMode
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettings
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerDataPackage.MultiplayerDataPackage
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerMode
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerSettings
import com.jcDevelopment.tictactoeadfree.module.data.soundSettings.SoundSettings
import com.jcDevelopment.tictactoeadfree.module.gameDificulty.GameDifficultyChooserFragment
import com.jcDevelopment.tictactoeadfree.module.music.MusicPlayer
import com.jcDevelopment.tictactoeadfree.module.sounds.SoundPlayer
import com.jcDevelopment.tictactoeadfree.module.usedLibraries.UsedLibrariesFragment
import com.jcDevelopment.tictactoeadfree.module.viewmodels.GameSettingsViewModel
import com.jcDevelopment.tictactoeadfree.module.viewmodels.MultiplayerSettingsViewModel
import com.jcDevelopment.tictactoeadfree.module.viewmodels.SoundSettingsViewModel
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_two_player_mode_choser.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.StringReader


class HomeActivity : BaseActivity(), HomeFragment.Listener, CompanyLogoFragment.Listener,
    TwoPlayerModeChooserFragment.Listener, GameDifficultyChooserFragment.Listener {
    private lateinit var handshakeDisposable: Disposable
    private lateinit var deviceNameDisposable: Disposable
    private lateinit var connectionDisposable: Disposable

    private val gameFragmentTag = "GAME_FRAGMENT_TAG"

    private var isTransactionSafe: Boolean = true
    private val activity = this
    private val manager = activity.supportFragmentManager

    //Bluetooth
    // Intent request codes
    private val REQUEST_ENABLE_BT_AS_HOST = 3
    private val REQUEST_ENABLE_BT_AS_CLIENT = 4

    /*** Local Bluetooth adapter*/
    private var mBluetoothAdapter: BluetoothAdapter? = null

    private val gson = Gson()

    private val gameSettingsViewModel by inject<GameSettingsViewModel>()
    private val multiplayerSettingsViewModel by viewModel<MultiplayerSettingsViewModel>()
    private val soundSettingsViewModel by viewModel<SoundSettingsViewModel>()

    private val musicPlayer by lazy { MusicPlayer(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        musicPlayer.initMediaPlayer()

        if (soundSettingsViewModel.getSoundSettings().last().isMusicPlaying) {
            musicPlayer.resumeMusic()
        }

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
        if (mBluetoothAdapter != null) {
            if (BlueToothService.getState() == BlueToothService.STATE_NONE && mBluetoothAdapter!!.isEnabled) {
                // Start the Bluetooth chat services
                BlueToothService.start()
            }
        }

        initClickListener()

        initBluetoothEventListener()

        soundOnResume()
    }

    private fun soundOnResume() {
        if (soundSettingsViewModel.getSoundSettings().last().isMusicPlaying) {
            musicPlayer.resumeMusic()
        }
        if (soundSettingsViewModel.getSoundSettings().last().isMusicPlaying) {
            home_toolbar.menu[2].icon =
                ContextCompat.getDrawable(this, R.drawable.ic_music_note_24px)
        } else {
            home_toolbar.menu[2].icon =
                ContextCompat.getDrawable(this, R.drawable.ic_music_off_24px)
        }
    }

    private fun initClickListener() {
        home_activity_root_contraint.setOnClickListener {
            home_activity_bluetooth_list?.visibility = View.GONE
        }
    }

    private fun getMultiplayerSettings(): MultiplayerSettings {
        return multiplayerSettingsViewModel.getMultiplayerSettings().last()
    }


    private fun initBluetoothEventListener() {
        if (mBluetoothAdapter != null) {
            connectionDisposable = BlueToothService.getConnectionObservable()
                .doOnNext { connectionState ->
                    when (connectionState) {
                        BlueToothService.STATE_CONNECTED -> {
                            two_player_game_mode_bluetooth_waiting_for_player_overlay?.visibility =
                                View.GONE

                            BlueToothService.write(
                                gson.toJson(
                                    MultiplayerDataPackage(
                                        gameSettings = getGameSettings()
                                    )
                                ).toByteArray()
                            )
                        }
                    }
                }
                .doOnError { makeToast(getString(R.string.result_canceled_info)) }
                .subscribe()

            deviceNameDisposable = BlueToothService.getDeviceNameObservable()
                .doOnNext {
                    multiplayerSettingsViewModel.updateMultiplayersettings(
                        MultiplayerSettings(
                            multiplayerMode = getMultiplayerSettings().multiplayerMode,
                            isHost = getMultiplayerSettings().isHost,
                            lastConnectedDeviceName = it
                        )
                    )
                }.subscribe()

            handshakeDisposable = BlueToothService.getMessageObservable()
                .doOnNext {
                    val reader = JsonReader(StringReader(it))
                    reader.isLenient = true
                    val gameSettingsComparison = gson.fromJson<MultiplayerDataPackage>(
                        reader,
                        MultiplayerDataPackage::class.java
                    )
                    if (!getMultiplayerSettings().isHost) {
                        gameSettingsComparison.gameVersionName?.let { remoteVersionName ->
                            gameSettingsComparison.gameVersionCode?.let { remoteGameVersion ->
                                if (remoteGameVersion == BuildConfig.VERSION_CODE && remoteVersionName == BuildConfig.VERSION_NAME) {
                                    gameSettingsComparison.gameSettings?.let { remoteGameSettings ->

                                        if (remoteGameSettings.gameMode == gameSettingsViewModel.getGameSettings()
                                                .last().gameMode
                                        ) {
                                            openGameFragment()
                                        } else {
                                            makeToast(
                                                getString(
                                                    R.string.bluetooth_playmode_switched,
                                                    remoteGameSettings.gameMode
                                                )
                                            )
                                            gameSettingsViewModel.updateGameSettings(
                                                GameSettings(
                                                    gameMode = remoteGameSettings.gameMode
                                                )
                                            )
                                            openGameFragment()
                                        }

                                        BlueToothService.write(
                                            gson.toJson(MultiplayerDataPackage(handShakeSuccessAck = true))
                                                .toByteArray()
                                        )
                                    }
                                } else {
                                    makeToast(getString(R.string.different_version_code_error))
                                }
                            }
                        }
                    }


                    gameSettingsComparison.handShakeSuccessAck?.let { handShakeAck ->
                        if (handShakeAck) {
                            openGameFragment()
                        }
                    }


                    gameSettingsComparison.askForGame?.let { askForAnotherGame ->
                        if (askForAnotherGame) {
                            when (gameSettingsComparison.askForGameAck) {
                                null -> {
                                    home_activity_ask_for_another_game?.isVisible = true
                                    home_activity_ask_for_another_game.setHeadline(
                                        getMultiplayerSettings().lastConnectedDeviceName
                                    )
                                    home_activity_ask_for_another_game?.declineAnotherGameObservable?.subscribe {
                                        home_activity_ask_for_another_game?.isVisible = false
                                        BlueToothService.write(
                                            gson.toJson(
                                                MultiplayerDataPackage(
                                                    askForGame = false,
                                                    askForGameAck = false
                                                )
                                            ).toByteArray()
                                        )

                                        Handler().postDelayed({
                                            BlueToothService.stop()
                                        }, 1000)
                                    }

                                    home_activity_ask_for_another_game?.ackAnotherGameObservable?.subscribe {
                                        multiplayerSettingsViewModel.updateMultiplayersettings(
                                            MultiplayerSettings(
                                                multiplayerMode = MultiplayerMode.BLUETOOTH.toString(),
                                                isHost = false
                                            )
                                        )

                                        if (gameSettingsComparison.gameSettings != null) {
                                            val gameSettings = gameSettingsComparison.gameSettings
                                            gameSettingsViewModel.updateGameSettings(
                                                GameSettings(
                                                    gameSettings.isSecondPlayerAi,
                                                    gameSettings.gameMode
                                                )
                                            )

                                            home_activity_ask_for_another_game?.isVisible = false
                                            BlueToothService.write(
                                                gson.toJson(
                                                    MultiplayerDataPackage(
                                                        askForGame = true,
                                                        askForGameAck = true
                                                    )
                                                ).toByteArray()
                                            )
                                            openGameFragment()
                                        } else makeToast(getString(R.string.result_canceled_info))
                                    }
                                }
                                true -> {
                                    openGameFragment()
                                }
                                false -> {
                                    makeToast(getString(R.string.ask_another_game_declined))
                                }
                            }
                        }
                    }
                }.subscribe()
        }
    }

    private fun makeToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        BlueToothService.stop()
    }

    override fun onPostResume() {
        super.onPostResume()
        isTransactionSafe = true
    }

    override fun onPause() {
        super.onPause()
        isTransactionSafe = false

        musicPlayer.pauseMusic()

        /*handshakeDisposable?.dispose()
        deviceNameDisposable?.dispose()
        connectionDisposable?.dispose()*/
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

    override fun onAskForAnotherGame() {
        multiplayerSettingsViewModel.updateMultiplayersettings(
            MultiplayerSettings(
                isHost = true,
                multiplayerMode = MultiplayerMode.BLUETOOTH.toString()
            )
        )
        home_activity_ask_for_another_game?.isVisible = true
        home_activity_ask_for_another_game.setHeadline(getMultiplayerSettings().lastConnectedDeviceName)
        home_activity_ask_for_another_game?.declineAnotherGameObservable?.subscribe {
            home_activity_ask_for_another_game?.isVisible = false
            BlueToothService.stop()
        }
        home_activity_ask_for_another_game?.ackAnotherGameObservable?.subscribe {
            home_activity_ask_for_another_game?.isVisible = false
            BlueToothService.write(
                gson.toJson(
                    MultiplayerDataPackage(
                        gameSettings = getGameSettings(),
                        askForGame = true
                    )
                ).toByteArray()
            )
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

        giveALeaveInfo()

        home_activity_bluetooth_list?.visibility = View.GONE

        if (manager.fragments.size > 1) {
            val resumeLastFragment = manager.fragments[(manager.fragments.size - 2)]
            resumeLastFragment.onResume()
            manager.beginTransaction().remove(manager.fragments.last()).commit()
        } else {
            this.finish()
        }
    }

    private fun giveALeaveInfo() {
        val gameMode = getMultiplayerSettings().multiplayerMode
        if (gameMode == MultiplayerMode.BLUETOOTH.toString()) {
            BlueToothService.write(
                Gson().toJson(MultiplayerDataPackage(leaveGame = true)).toByteArray()
            )
        }
    }

    private fun openBluetoothAsHost() {
        multiplayerSettingsViewModel.updateMultiplayersettings(
            MultiplayerSettings(
                isHost = true,
                multiplayerMode = MultiplayerMode.BLUETOOTH.toString()
            )
        )
        BlueToothService?.start()
    }

    private fun getBluetoothlist() {
        multiplayerSettingsViewModel.updateMultiplayersettings(
            MultiplayerSettings(
                isHost = false,
                multiplayerMode = MultiplayerMode.BLUETOOTH.toString()
            )
        )
        val bondedDevices = mBluetoothAdapter?.bondedDevices

        bondedDevices?.let { notNullBondedDevices ->
            val arrayAdapter = ArrayAdapter<String>(
                activity,
                R.layout.recyclerview_used_library,
                R.id.used_library_headline,
                notNullBondedDevices.map { it.name.toString() + it.name })
            home_activity_bluetooth_list?.adapter = arrayAdapter
            home_activity_bluetooth_list?.setOnItemClickListener { _, _, position, _ ->
                home_activity_bluetooth_list?.isVisible = false
                val bondedBluetoothAdapter = mBluetoothAdapter?.getRemoteDevice(
                    bondedDevices.elementAt(
                        position
                    ).address
                )

                BlueToothService.connect(bondedBluetoothAdapter!!, secure = false)
            }

            home_activity_bluetooth_list?.visibility = View.VISIBLE
        } ?: makeToast(getString(R.string.no_bounded_devices_found_error))
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

        home_toolbar.menu[2].setOnMenuItemClickListener {
            if (soundSettingsViewModel.getSoundSettings().last().isMusicPlaying) {
                soundSettingsViewModel.updateSoundSettings(SoundSettings(isMusicPlaying = false))
                musicPlayer.pauseMusic()
                home_toolbar.menu[2].icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_music_off_24px)
            } else {
                soundSettingsViewModel.updateSoundSettings(SoundSettings(isMusicPlaying = true))
                musicPlayer.resumeMusic()
                home_toolbar.menu[2].icon =
                    ContextCompat.getDrawable(this, R.drawable.ic_music_note_24px)
            }
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
        transaction.add(R.id.main_activity_root, CompanyLogoFragment.newInstance())
        transaction.commit()
    }

    private fun openGameFragment() {
        //handshakeDisposable.dispose()
        val gameSettings = getGameSettings()
        val gameMode = GameMode.valueOf(gameSettings.gameMode)
        val last = manager.fragments.last()
        if (manager.findFragmentByTag(gameFragmentTag) == null) {
            if (gameMode == GameMode.TIC_TAC_TOE) {
                openTicTacToeFragment()
            } else if (gameMode == GameMode.FOUR_IN_A_ROW) {
                openFourInARowFragment()
            }
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
        transaction.add(
            R.id.main_activity_root, SimpleFourInARowBoardFragment.newInstance(),
            gameFragmentTag
        )
        transaction.commit()
    }

    private fun openTicTacToeFragment() {
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(
            R.id.main_activity_root, SimpleTicTacToeBoardFragment.newInstance(),
            gameFragmentTag
        )
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