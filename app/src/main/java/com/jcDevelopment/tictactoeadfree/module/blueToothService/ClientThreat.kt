package com.jcDevelopment.tictactoeadfree.module.blueToothService

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

private class ClientThreat(device: BluetoothDevice, val bluetoothAdapter: BluetoothAdapter?) :
    Thread() {
    //TODO zentral in Service
    private val uuid: UUID = UUID.fromString("af8d7c3f-e566-41e2-bec6-a8f82dd77710")
    private val serviceDiscoveryName = "SpookyHalloWeenAPP"

    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(uuid)
    }

    public override fun run() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter?.cancelDiscovery()

        mmSocket?.use { socket ->
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            socket.connect()

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            // TODO manageMyConnectedSocket(socket)
        }
    }

    // Closes the client socket and causes the thread to finish.
    fun cancel() {
        try {
            mmSocket?.close()
        } catch (e: IOException) {
            Log.e("Bluetooth", "Could not close the client socket", e)
        }
    }
}
