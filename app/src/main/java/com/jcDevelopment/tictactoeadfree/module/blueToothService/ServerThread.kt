package com.jcDevelopment.tictactoeadfree.module.blueToothService

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*


class ServerThread(private val bluetoothAdapter: BluetoothAdapter?) : Thread() {
    //TODO zentral in Service
    private val uuid: UUID = java.util.UUID.fromString("af8d7c3f-e566-41e2-bec6-a8f82dd77710")
    private val serviceDiscoveryName = "SpookyHalloWeenAPP"
    private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
        bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(serviceDiscoveryName, uuid)
    }

    override fun run() {
        // Keep listening until exception occurs or a socket is returned.
        var shouldLoop = true
        while (shouldLoop) {
            val socket: BluetoothSocket? = try {
                mmServerSocket?.accept()
            } catch (e: IOException) {
                Log.d("Bluetooth", "Socket's accept() method failed", e)
                shouldLoop = false
                null
            }
            socket?.also {
                //manageMyConnectedSocket(it)
                mmServerSocket?.close()
                shouldLoop = false
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    fun cancel() {
        try {
            mmServerSocket?.close()
        } catch (e: IOException) {
            Log.e("Bluetooth", "Could not close the connect socket", e)
        }
    }
}
