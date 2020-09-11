package com.jcDevelopment.tictactoeadfree.module.data.multiplayerDataPackage

import com.jcDevelopment.tictactoeadfree.module.data.gameSettings.GameSettings
import com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings.MultiplayerSettings

data class MultiplayerDataPackage(
    val gameSettings: GameSettings? = null,
    val multiplayerSettings: MultiplayerSettings? = null,
    val turn: Int? = null,
    val x: Int? = null,
    val y: Int? = null,
    val z: Int? = null,
    val ack: Boolean? = null
)