package com.jcDevelopment.tictactoeadfree.module.blueToothService

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.widget.Toast


class BlueToothService internal constructor() {
    val REQUEST_ENABLE_BT = 12
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    fun checkForBluetoothAdapter(homeActivity: Activity) {
        if (bluetoothAdapter == null) {
            Toast.makeText(homeActivity, "BlueTooth nicht gefunden", Toast.LENGTH_LONG).show()
            return
        }

        if (bluetoothAdapter.isEnabled == false) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            homeActivity.startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
        }
    }

    fun getBondedDevices(): Set<BluetoothDevice>? {
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
        }

        return pairedDevices
    }

    interface Listener {
        fun noBluetoothAdapterFound()
    }
}