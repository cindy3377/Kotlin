package com.example.bluetoothdevicesscanning

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context.BLUETOOTH_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

class MainActivity : AppCompatActivity() {

    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private val scanResults = mutableStateListOf<ScanResult>()

    @RequiresApi(Build.VERSION_CODES.O)
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        setContent {
            var scanning by remember { mutableStateOf(false) }

            Column(modifier = Modifier.fillMaxSize()) {
                Button(onClick = {
                    checkPermissionsAndStartScan()
                    scanning = true
                }) {
                    Text(text = if (scanning) "Scanning..." else "Start Scanning")
                }

                LazyColumn {
                    items(scanResults) { result ->
                        val deviceName = result.device.name ?: "Unknown"
                        val macAddress = result.device.address
                        val rssi = result.rssi
                        val isConnectable = result.isConnectable

                        Text(
                            text = "$deviceName ($macAddress) - RSSI: $rssi dBm",
                            color = if (isConnectable) Color.Black else Color.Gray,
                            modifier = Modifier.background(if (isConnectable) Color.White else Color.LightGray)
                        )
                    }
                }
            }
        }
    }

    private fun setContent(content: @Composable () -> Unit) {

    }

    private fun checkPermissionsAndStartScan() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissions.isNotEmpty()) {
            requestPermissions(permissions.toTypedArray())
        } else {
            startBluetoothScan()
        }
    }

    // Request runtime permissions
    private fun requestPermissions(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this, permissions, 1)
    }

    // Start scanning for Bluetooth LE devices
    private fun startBluetoothScan() {
        scanResults.clear()
        bluetoothLeScanner.startScan(scanCallback)

        // Stop scan after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            bluetoothLeScanner.stopScan(scanCallback)
        }, 3000)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (!scanResults.contains(result)) {
                scanResults.add(result)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            scanResults.addAll(results.filterNot { scanResults.contains(it) })
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("MainActivity", "Scan failed: $errorCode")
        }
    }
}
