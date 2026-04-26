package com.tannu.edureach.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatDelegate

class SensorManagerUtil(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    var onProximityChanged: ((isNear: Boolean) -> Unit)? = null

    private val lightListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_LIGHT) {

                

                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val proximityListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
                val distance = event.values[0]
                val isNear = distance < (proximitySensor?.maximumRange ?: 5f).coerceAtMost(5f)
                onProximityChanged?.invoke(isNear)
            }
        }
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun registerSensors() {
        lightSensor?.let {
            sensorManager.registerListener(lightListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        proximitySensor?.let {
            sensorManager.registerListener(proximityListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun unregisterSensors() {
        sensorManager.unregisterListener(lightListener)
        sensorManager.unregisterListener(proximityListener)
    }
}