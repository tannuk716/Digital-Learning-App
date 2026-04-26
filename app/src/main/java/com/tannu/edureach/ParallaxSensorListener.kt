package com.tannu.edureach

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.view.View

class ParallaxSensorListener(
    private val backgroundView: View?,
    private val foregroundViews: List<View> = emptyList()
) : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]

            backgroundView?.translationX = x * -8f
            backgroundView?.translationY = y * 8f

            for (fg in foregroundViews) {
                fg.translationX = x * 4f
                fg.translationY = y * -4f
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}