package com.tannu.edureach.utils

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.tannu.edureach.R

class BluetoothActivity : AppCompatActivity() {

    private lateinit var btnToggleBluetooth: Button
    private lateinit var btnDiscover: Button
    private lateinit var lvDevices: ListView

    private lateinit var bluetoothManager: BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter? = null

    private val discoveredDevices = mutableListOf<String>()
    private lateinit var listAdapter: ArrayAdapter<String>

    private val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        btnToggleBluetooth = findViewById(R.id.btnToggleBluetooth)
        btnDiscover = findViewById(R.id.btnDiscover)
        lvDevices = findViewById(R.id.lvDevices)

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, discoveredDevices)
        lvDevices.adapter = listAdapter

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show()
        }

        updateToggleButton()

        btnToggleBluetooth.setOnClickListener {
            toggleBluetooth()
        }

        btnDiscover.setOnClickListener {
            if (checkPermissions()) {
                startDiscovery()
            } else {
                requestPermissions()
            }
        }
    }

    private fun updateToggleButton() {
        if (bluetoothAdapter?.isEnabled == true) {
            btnToggleBluetooth.text = "Turn Off Bluetooth"
        } else {
            btnToggleBluetooth.text = "Turn On Bluetooth"
        }
    }

    @SuppressLint("MissingPermission")
    private fun toggleBluetooth() {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }
        
        if (bluetoothAdapter?.isEnabled == true) {
            bluetoothAdapter?.disable()
            Toast.makeText(this, "Bluetooth turning off", Toast.LENGTH_SHORT).show()
        } else {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }
        updateToggleButton()
    }

    @SuppressLint("MissingPermission")
    private fun startDiscovery() {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
        }
        discoveredDevices.clear()
        listAdapter.notifyDataSetChanged()

        bluetoothAdapter?.startDiscovery()
        Toast.makeText(this, "Discovering devices...", Toast.LENGTH_SHORT).show()

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name ?: "Unknown Device"
                    val deviceAddress = device?.address ?: ""
                    val info = "$deviceName\n$deviceAddress"
                    
                    if (info !in discoveredDevices) {
                        discoveredDevices.add(info)
                        listAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                   ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        }
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                PERMISSION_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {

        }
    }
}