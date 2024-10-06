package com.example.heartrate_ble

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import java.util.*

class MainActivity : ComponentActivity() {

    // ViewModel for holding BPM data
    private val heartRateViewModel: HeartRateViewModel by viewModels()

    // List to store scanned devices
    private val scannedDevices = mutableListOf<BluetoothDevice>()

    // Define the permission request code
    private companion object {
        private const val PERMISSION_REQUEST_BLUETOOTH_CONNECT = 1001
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ensure Bluetooth permissions are granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                PERMISSION_REQUEST_BLUETOOTH_CONNECT
            )
            return
        }

        // Start scanning for BLE devices
        val bluetoothAdapter: BluetoothAdapter = getSystemService(BluetoothManager::class.java).adapter
        val bluetoothLeScanner: BluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        bluetoothLeScanner.startScan(scanCallback)

        // Observe BPM data from ViewModel
        heartRateViewModel.mBPM.observe(this, Observer { bpm ->
            Log.d("DBG", "Updated BPM: $bpm")
            // Update UI, e.g., a TextView
            val heartRateTextView: TextView = findViewById(R.id.heart_rate_text_view)
            heartRateTextView.text = "Heart Rate: $bpm BPM"
        })
    }

    // Scan callback for discovering BLE devices
    private val scanCallback = object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device: BluetoothDevice = result.device

            // Ensure BLUETOOTH_CONNECT permission is granted
            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Log.d("DBG", "Found device: ${device.name}, address: ${device.address}")

                // Add scanned devices to the list if not already present
                if (!scannedDevices.contains(device)) {
                    scannedDevices.add(device)
                }

                // Option 1: Filter by device name (e.g., containing "Heart Rate")
                if (device.name?.contains("Heart Rate", ignoreCase = true) == true) {
                    connectToDevice(device)
                }

                // Option 2: (Alternative) Filter by Heart Rate Service UUID
                val heartRateServiceUUID = UUID.fromString(HEART_RATE_SERVICE_UUID)
                if (result.scanRecord?.serviceUuids?.contains(ParcelUuid(heartRateServiceUUID)) == true) {
                    connectToDevice(device)
                }

            } else {
                // Request BLUETOOTH_CONNECT permission if not granted
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    PERMISSION_REQUEST_BLUETOOTH_CONNECT
                )
            }
        }
    }

    // Function to connect to the specified BLE device
    @RequiresApi(Build.VERSION_CODES.S)
    private fun connectToDevice(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                PERMISSION_REQUEST_BLUETOOTH_CONNECT
            )
            return
        }

        device.connectGatt(this, false, gattCallback)
    }

    // GATT callback for handling BLE connection and services
    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresApi(Build.VERSION_CODES.S)
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("DBG", "Connected to GATT server")

                // Check for BLUETOOTH_CONNECT permission before calling discoverServices
                if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    gatt?.discoverServices()  // Permission is granted, proceed with discoverServices
                } else {
                    // Permission is not granted, request permission
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                        PERMISSION_REQUEST_BLUETOOTH_CONNECT
                    )
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("DBG", "Disconnected from GATT server")
            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            val heartRateService = gatt?.getService(UUID.fromString(HEART_RATE_SERVICE_UUID))
            heartRateService?.let {
                val heartRateCharacteristic = it.getCharacteristic(UUID.fromString(HEART_RATE_MEASUREMENT_UUID))

                // Check if permission is granted before setting notification
                if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    gatt.setCharacteristicNotification(heartRateCharacteristic, true)

                    val descriptor = heartRateCharacteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG_UUID))
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                } else {
                    // Request permission if not already granted
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                        PERMISSION_REQUEST_BLUETOOTH_CONNECT
                    )
                }
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            characteristic?.let {
                if (it.uuid == UUID.fromString(HEART_RATE_MEASUREMENT_UUID)) {
                    val bpm = it.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1)
                    Log.d("DBG", "BPM: $bpm")
                    heartRateViewModel.updateBPM(bpm)
                }
            }
        }
    }

    // Function to display a list of scanned devices and let the user choose one to connect
    @RequiresApi(Build.VERSION_CODES.S)
    private fun showDeviceSelectionDialog() {
        val deviceNames = scannedDevices.map { it.name ?: "Unknown Device" }.toTypedArray()

        // Create an alert dialog for device selection
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select a device to connect")
        builder.setItems(deviceNames) { _, which ->
            connectToDevice(scannedDevices[which])
        }
        builder.show()
    }
}

// Define UUIDs for Heart Rate Service and Characteristic
const val HEART_RATE_SERVICE_UUID = "0000180d-0000-1000-8000-00805f9b34fb"
const val HEART_RATE_MEASUREMENT_UUID = "00002a37-0000-1000-8000-00805f9b34fb"
const val CLIENT_CHARACTERISTIC_CONFIG_UUID = "9D67A160F14748B280635744DE8FA0A6"

// ViewModel to hold the heart rate BPM data
class HeartRateViewModel : ViewModel() {
    val mBPM = MutableLiveData<Int>(0)

    // Update BPM in the ViewModel
    fun updateBPM(bpm: Int) {
        mBPM.postValue(bpm)
    }
}
