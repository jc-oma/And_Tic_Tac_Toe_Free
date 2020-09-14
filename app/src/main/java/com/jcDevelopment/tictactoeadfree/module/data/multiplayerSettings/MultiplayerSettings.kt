package com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "multiplayerSettings")
data class MultiplayerSettings (
    val multiplayerMode: String = MultiplayerMode.NONE.toString(),
    val isHost: Boolean = false,
    val lastConnectedDeviceName: String? = null,
    @PrimaryKey
    val id: Int = 0
)

enum class MultiplayerMode {
    NONE,
    WIFI,
    BLUETOOTH,
    HOT_SEAT
}