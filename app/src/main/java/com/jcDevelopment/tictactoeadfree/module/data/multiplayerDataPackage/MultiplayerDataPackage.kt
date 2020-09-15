package com.jcDevelopment.tictactoeadfree.module.data.multiplayerDataPackage

import com.jcDevelopment.tictactoeadfree.BuildConfig
import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettings
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerSettings

data class MultiplayerDataPackage(
    val gameSettings: GameSettings? = null,
    val multiplayerSettings: MultiplayerSettings? = null,
    val gameVersionCode: Int? = BuildConfig.VERSION_CODE,
    val gameVersionName: String? = BuildConfig.VERSION_NAME,
    val turn: Int? = null,
    val x: Int? = null,
    val y: Int? = null,
    val z: Int? = null,
    val handShakeSuccessAck: Boolean? = null,
    val leaveGame: Boolean? = null,
    val askForGame: Boolean? = null,
    val askForGameAck: Boolean? = null,
    val restartGame: Boolean? = null
)