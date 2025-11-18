package com.example.steptracker.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class StepCounterDataSource(context: Context) {

    private val sensorManager: SensorManager? = context.getSystemService()
    private val stepSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    fun isSensorAvailable(): Boolean = stepSensor != null

    fun stepCountFlow(): Flow<Int> = callbackFlow {
        val manager = sensorManager
        val sensor = stepSensor
        if (manager == null || sensor == null) {
            trySend(0)
            awaitClose { }
            return@callbackFlow
        }
        var baseValue = -1f
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val current = event.values.firstOrNull() ?: return
                if (baseValue < 0) {
                    baseValue = current
                }
                val steps = (current - baseValue).toInt().coerceAtLeast(0)
                trySend(steps)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }
        manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        awaitClose { manager.unregisterListener(listener) }
    }
}
