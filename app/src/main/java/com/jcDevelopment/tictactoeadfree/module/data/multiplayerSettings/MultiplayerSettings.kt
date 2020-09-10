package com.jcDevelopment.tictactoeadfree.module.data.multiplayerSettings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "multiplayerSettings")
data class MultiplayerSettings (
    val multiplayerMode: String = MultiplayerMode.BLUETOOTH.toString(),
    val isHost: Boolean = false,
    @PrimaryKey
    val id: Int = 0
)

enum class MultiplayerMode {
    WIFI,
    BLUETOOTH
}